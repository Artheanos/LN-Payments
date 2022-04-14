package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;
import pl.edu.pjatk.lnpayments.webservice.wallet.observer.ChannelCloseObserver;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.ChannelsBalance;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private SynchronousLndAPI lndAPI;

    @Mock
    private AsynchronousLndAPI asyncApi;

    @Mock
    private ChannelCloseObserver channelCloseObserver;

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private ChannelService channelService;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(ChannelService.class)).addAppender(logAppender);
    }

    @Test
    void shouldCloseAllChannelsCooperatively() throws StatusException, ValidationException {
        String channelPoint1 = "channel1:1";
        String channelPoint2 = "channel2:2";
        Channel channel1 = createChannel(true, channelPoint1, 1);
        Channel channel2 = createChannel(true, channelPoint2, 2);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1, channel2));
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);

        channelService.closeAllChannels(false);

        ArgumentCaptor<CloseChannelRequest> captor = ArgumentCaptor.forClass(CloseChannelRequest.class);
        verify(asyncApi, times(2)).closeChannel(captor.capture(), eq(channelCloseObserver));
        List<CloseChannelRequest> values = captor.getAllValues();
        assertThat(values).hasSize(2);
        assertThat(values).extracting(CloseChannelRequest::getForce).allMatch(e -> !e);
        assertThat(values).extracting(CloseChannelRequest::getChannelPoint)
                .map(point -> point.getFundingTxidStr() + ":" + point.getOutputIndex())
                .containsAll(List.of(channelPoint1, channelPoint2));
    }

    @Test
    void shouldNotCloseChannelThatIsInactiveForLessThanWeekAndNoForce() throws StatusException, ValidationException {
        String channelPoint1 = "channel1:1";
        String channelPoint2 = "channel2:2";
        Channel channel1 = createChannel(true, channelPoint1, 1);
        Channel channel2 = createChannel(false, channelPoint2, 2);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1, channel2));
        ChannelEdge channelEdge = new ChannelEdge();
        channelEdge.setLastUpdate(Math.toIntExact(LocalDate.now().toEpochDay()));
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);
        when(lndAPI.getChanInfo(anyLong())).thenReturn(channelEdge);

        channelService.closeAllChannels(false);

        ArgumentCaptor<CloseChannelRequest> captor = ArgumentCaptor.forClass(CloseChannelRequest.class);
        verify(lndAPI).getChanInfo(anyLong());
        verify(asyncApi, times(2)).closeChannel(captor.capture(), eq(channelCloseObserver));
        List<CloseChannelRequest> values = captor.getAllValues();
        assertThat(values).hasSize(2);
        assertThat(values).extracting(CloseChannelRequest::getForce).allMatch(e -> !e);
        assertThat(values).extracting(CloseChannelRequest::getChannelPoint)
                .map(point -> point.getFundingTxidStr() + ":" + point.getOutputIndex())
                .containsAll(List.of(channelPoint1, channelPoint2));
    }

    @Test
    void shouldAutoForceCloseChannelInactiveForMoreThanWeek() throws Exception {
        String channelPoint1 = "channel1:1";
        Channel channel1 = createChannel(false, channelPoint1, 1);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1));
        ChannelEdge channelEdge = new ChannelEdge();
        channelEdge.setLastUpdate(Math.toIntExact(LocalDate.now().minusYears(1).toEpochDay()));
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);
        when(lndAPI.getChanInfo(anyLong())).thenReturn(channelEdge);

        channelService.closeAllChannels(false);

        ArgumentCaptor<CloseChannelRequest> captor = ArgumentCaptor.forClass(CloseChannelRequest.class);
        verify(lndAPI).getChanInfo(anyLong());
        verify(asyncApi).closeChannel(captor.capture(), eq(channelCloseObserver));
        List<CloseChannelRequest> values = captor.getAllValues();
        assertThat(values).hasSize(1);
        assertThat(values).extracting(CloseChannelRequest::getForce).allMatch(e -> e);
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Force closing {}", Level.WARN));
    }

    @Test
    void shouldForceCloseChannelInactiveForLessThanWeek() throws Exception {
        String channelPoint1 = "channel1:1";
        String channelPoint2 = "channel2:2";
        Channel channel1 = createChannel(true, channelPoint1, 1);
        Channel channel2 = createChannel(false, channelPoint2, 2);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1, channel2));
        ChannelEdge channelEdge = new ChannelEdge();
        channelEdge.setLastUpdate(Math.toIntExact(LocalDate.now().toEpochDay()));
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);

        channelService.closeAllChannels(true);

        ArgumentCaptor<CloseChannelRequest> captor = ArgumentCaptor.forClass(CloseChannelRequest.class);
        verify(lndAPI, never()).getChanInfo(anyLong());
        verify(asyncApi, times(2)).closeChannel(captor.capture(), eq(channelCloseObserver));
        List<CloseChannelRequest> values = captor.getAllValues();
        assertThat(values).hasSize(2);
        assertThat(values.get(0).getForce()).isFalse();
        assertThat(values.get(1).getForce()).isTrue();
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Force closing {}", Level.WARN));
    }

    @Test
    void shouldThrowExceptionWhenErrorOnClosingChannel() throws ValidationException, StatusException {
        when(lndAPI.listChannels(any())).thenThrow(ValidationException.class);
        assertThatExceptionOfType(LightningException.class)
                .isThrownBy(() -> channelService.closeAllChannels(false))
                .withMessage("Error when closing channels");
    }

    @Test
    void shouldLogWarningWhenUnableToCloseChannel() throws StatusException, ValidationException {
        String channelPoint1 = "channel1:1";
        Channel channel1 = createChannel(true, channelPoint1, 1);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1));
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);
        doThrow(ServerSideException.class).when(asyncApi).closeChannel(any(), eq(channelCloseObserver));

        channelService.closeAllChannels(false);

        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Unable to close channel: {}", Level.ERROR));
    }

    @Test
    void shouldObtainChannelsBalance() throws StatusException, ValidationException {
        ChannelBalanceResponse balanceResponse = new ChannelBalanceResponse();
        balanceResponse.setBalance(1000L);
        String channelPoint1 = "channel1:1";
        Channel channel1 = createChannel(true, channelPoint1, 1);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1));
        when(lndAPI.channelBalance()).thenReturn(balanceResponse);
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);
        when(propertyService.getAutoChannelCloseLimit()).thenReturn(10000L);

        ChannelsBalance channelsBalance = channelService.getChannelsBalance();

        verify(propertyService).getAutoChannelCloseLimit();
        assertThat(channelsBalance.getOpenedChannels()).isEqualTo(1);
        assertThat(channelsBalance.getTotalBalance()).isEqualTo(1000L);
        assertThat(channelsBalance.getAutoChannelCloseLimit()).isEqualTo(10000L);
    }

    @Test
    void shouldThrowExceptionWhenUnableToCallLndApi() throws StatusException, ValidationException {
        Class<ValidationException> exceptionClass = ValidationException.class;
        when(lndAPI.channelBalance()).thenThrow(exceptionClass);

        assertThatExceptionOfType(LightningException.class)
                .isThrownBy(() -> channelService.getChannelsBalance())
                .withMessage("Unable to get channels balance!")
                .withCauseExactlyInstanceOf(exceptionClass);
    }

    private Channel createChannel(boolean isActive, String channelPoint, long chanId) {
        Channel channel = new Channel();
        channel.setChannelPoint(channelPoint);
        channel.setActive(isActive);
        channel.setChanId(chanId);
        return channel;
    }
}

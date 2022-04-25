package pl.edu.pjatk.lnpayments.webservice.wallet.scheduler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.ChannelsBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.LightningWalletBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.WalletDetails;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletActionsSchedulerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletActionsScheduler walletActionsScheduler;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(WalletActionsScheduler.class)).addAppender(logAppender);
    }

    @Test
    void shouldTransferFundsWhenLimitIsExceeded() {
        WalletDetails details = getDetailsWithLightningBalance(100, 50);
        when(walletService.getDetails()).thenReturn(details);

        walletActionsScheduler.scheduleAutoTransfer();

        verify(walletService).transferToWallet();
        Assertions.assertThat(logAppender.list)
            .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
            .contains(Tuple.tuple("Transferring funds with scheduler...", Level.INFO));
    }

    @Test
    void shouldNotTransferFundsWhenLimitNotExceeded() {
        WalletDetails details = getDetailsWithLightningBalance(10, 51);
        when(walletService.getDetails()).thenReturn(details);

        walletActionsScheduler.scheduleAutoTransfer();

        verify(walletService, never()).transferToWallet();
        Assertions.assertThat(logAppender.list).isEmpty();
    }

    @Test
    void shouldLogMessageWhenExceptionsAreThrown() {
        when(walletService.getDetails()).thenThrow(NotFoundException.class);

        walletActionsScheduler.scheduleAutoTransfer();

        Assertions.assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Unable to transfer funds with scheduler: {}", Level.ERROR));
    }

    @Test
    void shouldCloseChannelsWhenThresholdExceeded() {
        WalletDetails details = getDetailsWithChannelsBalance(100, 50);
        when(walletService.getDetails()).thenReturn(details);

        walletActionsScheduler.scheduleAutoChannelClose();

        verify(walletService).closeAllChannels(false);
        Assertions.assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Closing channels with scheduler...", Level.INFO));
    }

    @Test
    void shouldNotCloseChannelsWhenThresholdNotEsceeded() {
        WalletDetails details = getDetailsWithChannelsBalance(10, 51);
        when(walletService.getDetails()).thenReturn(details);

        walletActionsScheduler.scheduleAutoChannelClose();

        verify(walletService, never()).closeAllChannels(anyBoolean());
        Assertions.assertThat(logAppender.list).isEmpty();
    }

    @Test
    void shouldLogMessageWhenClosingChannelsFailed() {
        when(walletService.getDetails()).thenThrow(NotFoundException.class);

        walletActionsScheduler.scheduleAutoChannelClose();

        Assertions.assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Unable to close channels with scheduler: {}", Level.ERROR));
    }

    private WalletDetails getDetailsWithLightningBalance(long balance, long limit) {
        LightningWalletBalance lightningWalletBalance = LightningWalletBalance.builder()
                .availableBalance(balance)
                .autoTransferLimit(limit)
                .build();
        return WalletDetails.builder()
                .lightningWalletBalance(lightningWalletBalance)
                .build();
    }

    private WalletDetails getDetailsWithChannelsBalance(long balance, long limit) {
        ChannelsBalance channelsBalance = ChannelsBalance.builder()
                .totalBalance(balance)
                .autoChannelCloseLimit(limit)
                .build();
        return WalletDetails.builder()
                .channelsBalance(channelsBalance)
                .build();
    }
}

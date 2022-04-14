package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;
import pl.edu.pjatk.lnpayments.webservice.wallet.observer.ChannelCloseObserver;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.ChannelsBalance;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.FORCE_CLOSE_INACTIVE_CHANNEL_DAYS;

@Slf4j
@Service
public class ChannelService {

    private final SynchronousLndAPI lndAPI;
    private final AsynchronousLndAPI asyncApi;
    private final ChannelCloseObserver channelCloseObserver;
    private final PropertyService propertyService;

    @Autowired
    public ChannelService(SynchronousLndAPI lndAPI,
                          AsynchronousLndAPI asyncApi,
                          ChannelCloseObserver channelCloseObserver,
                          PropertyService propertyService) {
        this.lndAPI = lndAPI;
        this.asyncApi = asyncApi;
        this.channelCloseObserver = channelCloseObserver;
        this.propertyService = propertyService;
    }

    /**
     * Closes all the opened channels. If channel is active, it attempts cooperative close. Otherwise, channel will
     * remain opened. If channel is closed for more than 7 days or withForce param is true, it will close the
     * channel with force. In this case funds will be available in wallet after couple days.
     *
     * @param withForce forces closing inactive channels with force
     */
    public void closeAllChannels(boolean withForce) {
        try {
            List<Channel> channels = lndAPI.listChannels(new ListChannelsRequest()).getChannels();
            for (Channel channel : channels) {
                closeChannel(channel, withForce);
            }
        } catch (StatusException | ValidationException e) {
            throw new LightningException("Error when closing channels", e);
        }
    }

    public ChannelsBalance getChannelsBalance() {
        try {
            return ChannelsBalance.builder()
                    .totalBalance(lndAPI.channelBalance().getBalance())
                    .openedChannels(lndAPI.listChannels(new ListChannelsRequest()).getChannels().size())
                    .autoChannelCloseLimit(propertyService.getAutoChannelCloseLimit())
                    .build();
        } catch (StatusException | ValidationException e) {
            throw new LightningException("Unable to get channels balance!", e);
        }
    }

    private void closeChannel(Channel channel, boolean withForce) throws StatusException, ValidationException {
        String channelPoint = channel.getChannelPoint();
        CloseChannelRequest closeChannelRequest = new CloseChannelRequest();
        ChannelPoint point = new ChannelPoint();
        String[] pointArr = channelPoint.split(":");
        point.setFundingTxidStr(pointArr[0]);
        point.setOutputIndex(Integer.parseInt(pointArr[1]));
        closeChannelRequest.setChannelPoint(point);
        try {
            if (!channel.getActive() && (withForce || isInactiveForTooLong(channel))) {
                closeChannelRequest.setForce(true);
                log.warn("Force closing {}", channelPoint);
            }
            asyncApi.closeChannel(closeChannelRequest, channelCloseObserver);
        } catch (ServerSideException e) {
            log.error("Unable to close channel: {}", channelPoint);
        }
    }

    private boolean isInactiveForTooLong(Channel channel) throws StatusException, ValidationException {
        ChannelEdge chanInfo = lndAPI.getChanInfo(channel.getChanId());
        LocalDate lastActive = LocalDate.ofEpochDay(chanInfo.getLastUpdate());
        LocalDate now = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(lastActive, now);
        return daysBetween >= FORCE_CLOSE_INACTIVE_CHANNEL_DAYS;
    }
}

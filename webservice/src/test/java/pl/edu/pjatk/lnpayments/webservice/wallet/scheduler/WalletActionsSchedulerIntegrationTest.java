package pl.edu.pjatk.lnpayments.webservice.wallet.scheduler;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(classes = IntegrationTestConfiguration.class)
@TestPropertySource(properties = {
        "lnp.wallet.autoTransfer.frequency=500",
        "lnp.wallet.autoTransfer.initialDelay=0",
        "lnp.wallet.autoClose.frequency=500",
        "lnp.wallet.autoClose.initialDelay=0"
})
class WalletActionsSchedulerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private SynchronousLndAPI lndAPI;

    @Autowired
    private AsynchronousLndAPI asynchronousAPI;

    @SpyBean
    private WalletActionsScheduler walletActionsScheduler;

    @Test
    void shouldPerformAutoTransfer() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList());
        walletRepository.save(wallet);
        WalletBalanceResponse balanceRequest = new WalletBalanceResponse();
        balanceRequest.setConfirmedBalance(213700000L);
        SendCoinsResponse sendCoinsResponse = new SendCoinsResponse();
        sendCoinsResponse.setTxid("tx");
        when(lndAPI.walletBalance()).thenReturn(balanceRequest);
        when(lndAPI.sendCoins(any())).thenReturn(sendCoinsResponse);
        ChannelBalanceResponse balanceResponse = new ChannelBalanceResponse();
        balanceResponse.setBalance(1000L);
        Channel channel1 = new Channel();
        channel1.setChannelPoint("channel1:1");
        channel1.setActive(true);
        channel1.setChanId(1);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1));
        when(lndAPI.channelBalance()).thenReturn(balanceResponse);
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);

        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(walletActionsScheduler, atLeastOnce()).scheduleAutoTransfer();
            verify(lndAPI).sendCoins(any());
        });
    }

    @Test
    void shouldInvokeSchedulerButNotTransferFundsWhenNoWallet() {
        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(walletActionsScheduler, atLeastOnce()).scheduleAutoTransfer();
            verify(lndAPI, never()).sendCoins(any());
        });

    }

    @Test
    void shouldInvokeSchedulerButNotCloseChannelsFundsWhenNoWallet() {
        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(walletActionsScheduler, atLeastOnce()).scheduleAutoChannelClose();
            verify(lndAPI, never()).closeChannel(any());
        });
    }

    @Test
    void shouldCloseChannelsWhenThresholdExceeded() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList());
        walletRepository.save(wallet);
        WalletBalanceResponse balanceRequest = new WalletBalanceResponse();
        balanceRequest.setConfirmedBalance(2137L);
        SendCoinsResponse sendCoinsResponse = new SendCoinsResponse();
        sendCoinsResponse.setTxid("tx");
        when(lndAPI.walletBalance()).thenReturn(balanceRequest);
        when(lndAPI.sendCoins(any())).thenReturn(sendCoinsResponse);
        ChannelBalanceResponse balanceResponse = new ChannelBalanceResponse();
        balanceResponse.setBalance(1000000000L);
        Channel channel1 = new Channel();
        channel1.setChannelPoint("channel1:1");
        channel1.setActive(true);
        channel1.setChanId(1);
        Channel channel2 = new Channel();
        channel2.setChannelPoint("channel2:2");
        channel2.setActive(true);
        channel2.setChanId(2);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1, channel2));
        when(lndAPI.channelBalance()).thenReturn(balanceResponse);
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);

        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(walletActionsScheduler, atLeastOnce()).scheduleAutoChannelClose();
            verify(asynchronousAPI, times(2)).closeChannel(any(CloseChannelRequest.class), any());
        });
    }

}

package pl.edu.pjatk.lnpayments.webservice.wallet.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.ChannelsBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.LightningWalletBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

@Slf4j
@Component
class WalletActionsScheduler {

    private final WalletService walletService;

    @Autowired
    public WalletActionsScheduler(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(initialDelayString = "${lnp.wallet.autoTransfer.initialDelay}",
            fixedDelayString = "${lnp.wallet.autoTransfer.frequency}")
    void scheduleAutoTransfer() {
        try {
            LightningWalletBalance balance = walletService.getDetails().getLightningWalletBalance();
            if (balance.getAvailableBalance() >= balance.getAutoTransferLimit()) {
                log.info("Transferring funds with scheduler...");
                walletService.transferToWallet();
            }
        } catch (NotFoundException | LightningException e) {
            log.error("Unable to transfer funds with scheduler: {}", e.getMessage());
        }
    }

    @Scheduled(initialDelayString = "${lnp.wallet.autoClose.initialDelay}",
            fixedDelayString = "${lnp.wallet.autoClose.frequency}")
    void scheduleAutoChannelClose() {
        try {
            ChannelsBalance balance = walletService.getDetails().getChannelsBalance();
            if (balance.getTotalBalance() >= balance.getAutoChannelCloseLimit()) {
                log.info("Closing channels with scheduler...");
                walletService.closeAllChannels(false);
            }
        } catch (NotFoundException | LightningException e) {
            log.error("Unable to close channels with scheduler: {}", e.getMessage());
        }
    }
}

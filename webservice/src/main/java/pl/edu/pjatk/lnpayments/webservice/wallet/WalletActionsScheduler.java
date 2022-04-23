package pl.edu.pjatk.lnpayments.webservice.wallet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.LightningWalletBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
class WalletActionsScheduler {

    private final WalletService walletService;

    @Autowired
    public WalletActionsScheduler(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(initialDelay = 5, fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
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
}

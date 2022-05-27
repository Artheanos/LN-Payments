package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

@Component
public class WalletRecreationHandler implements NotificationHandler {

    private final TransactionHandler transactionHandler;
    private final WalletService walletService;

    @Autowired
    public WalletRecreationHandler(TransactionHandler transactionHandler, WalletService walletService) {
        this.transactionHandler = transactionHandler;
        this.walletService = walletService;
    }

    @Override
    public void confirm(Notification notification, ConfirmationDetails data) {
        transactionHandler.confirm(notification, data);
        if (notification.getTransaction().isConfirmed() &&
                notification.getTransaction().getStatus() == TransactionStatus.APPROVED) {
            walletService.swapWallets();
        } else if (notification.getTransaction().getStatus() == TransactionStatus.FAILED) {
            walletService.deleteWalletInCreation();
        }
    }

    @Override
    public void deny(Notification notification) {
        transactionHandler.deny(notification);
        if(notification.getTransaction().isDenied()) {
            walletService.deleteWalletInCreation();
        }
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WALLET_RECREATION;
    }
}

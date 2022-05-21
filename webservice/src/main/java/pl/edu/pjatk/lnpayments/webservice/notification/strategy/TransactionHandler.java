package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import org.springframework.stereotype.Component;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.service.TransactionService;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;

import javax.persistence.OptimisticLockException;

@Component
public class TransactionHandler implements NotificationHandler {

    private final BitcoinService bitcoinService;
    private final TransactionService transactionService;

    public TransactionHandler(BitcoinService bitcoinService, TransactionService transactionService) {
        this.bitcoinService = bitcoinService;
        this.transactionService = transactionService;
    }

    @Override
    public void confirm(Notification notification, ConfirmationDetails data) {
        AdminUser user = notification.getAdminUser();
        Transaction transaction = notification.getTransaction();
        this.updateRawTransaction(
                transaction,
                data.getRawTransaction(),
                data.getVersion(),
                user.getPublicKey());
        notification.setStatus(NotificationStatus.CONFIRMED);
        if (transaction.isConfirmed()) {
            transactionService.broadcastTransaction(transaction);
        }
    }

    @Override
    public void deny(Notification notification) {
        notification.setStatus(NotificationStatus.DENIED);
        Transaction transaction = notification.getTransaction();
        if (transaction.isDenied()) {
            transactionService.denyTransaction(transaction);
        }
    }

    @Override
    public NotificationType getType() {
        return NotificationType.TRANSACTION;
    }

    private void updateRawTransaction(Transaction transaction, String rawTx, long version, String publicKey) {
        boolean isVerified = bitcoinService.verifySignature(rawTx, publicKey);
        if (!isVerified) throw new InconsistentDataException("Signature not found for key: " + publicKey);
        if (transaction.getVersion() != version) throw new OptimisticLockException();
        transaction.setRawTransaction(rawTx);
    }
}

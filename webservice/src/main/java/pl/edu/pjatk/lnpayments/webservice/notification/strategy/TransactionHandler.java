package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import org.springframework.stereotype.Component;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;

import javax.persistence.OptimisticLockException;

@Component
public class TransactionHandler implements NotificationHandler {

    private final BitcoinService bitcoinService;

    public TransactionHandler(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;
    }

    @Override
    public void confirm(Notification notification, ConfirmationDetails data) {
        AdminUser user = notification.getAdminUser();
        this.updateRawTransaction(
                notification.getTransaction(),
                data.getRawTransaction(),
                data.getVersion(),
                user.getPublicKey());
        notification.setStatus(NotificationStatus.CONFIRMED);
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

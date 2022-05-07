package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;

import javax.persistence.OptimisticLockException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ExtendWith(MockitoExtension.class)
class TransactionHandlerTest {

    @Mock
    private BitcoinService bitcoinService;

    @InjectMocks
    private TransactionHandler transactionHandler;

    @Test
    void shouldReturnCorrectType() {
        NotificationType type = transactionHandler.getType();
        assertThat(type).isEqualTo(NotificationType.TRANSACTION);
    }

    @Test
    void shouldConfirmTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setPublicKey("pub");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.TRANSACTION);
        ConfirmationDetails details = new ConfirmationDetails("rawtx", 0L);
        when(bitcoinService.verifySignature(anyString(), anyString())).thenReturn(true);

        transactionHandler.confirm(notification, details);

        verify(bitcoinService).verifySignature("rawtx", "pub");
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.CONFIRMED);
        assertThat(transaction.getRawTransaction()).isEqualTo("rawtx");
    }

    @Test
    void shouldThrowExceptionWhenVersionDoesNotMatch() {
        Transaction transaction = Transaction.builder().build();
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setPublicKey("pub");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.TRANSACTION);
        ConfirmationDetails details = new ConfirmationDetails("rawtx", 1L);
        when(bitcoinService.verifySignature(anyString(), anyString())).thenReturn(true);

        assertThatExceptionOfType(OptimisticLockException.class)
                .isThrownBy(() -> transactionHandler.confirm(notification, details));
    }

    @Test
    void shouldThrowExceptionWhenSignatureIsInvalid() {
        Transaction transaction = Transaction.builder().build();
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setPublicKey("pub");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.TRANSACTION);
        ConfirmationDetails details = new ConfirmationDetails("rawtx", 0L);
        when(bitcoinService.verifySignature(anyString(), anyString())).thenReturn(false);

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> transactionHandler.confirm(notification, details))
                .withMessage("Signature not found for key: pub");
    }
}

package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ExtendWith(MockitoExtension.class)
class WalletRecreationHandlerTest {

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionHandler transactionHandler;

    @InjectMocks
    private WalletRecreationHandler walletRecreationHandler;

    @Test
    void shouldConfirmAndSwapWalletsIfTransactionIsConfirmed() {
        Transaction transaction = new Transaction();
        transaction.setRequiredApprovals(1);
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setPublicKey("pub");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.WALLET_RECREATION);
        transaction.setNotifications(List.of(notification));
        ConfirmationDetails details = new ConfirmationDetails("rawtx", 0L, null);
        doAnswer(a -> {
            notification.setStatus(NotificationStatus.CONFIRMED);
            transaction.setStatus(TransactionStatus.APPROVED);
            return null;
        }).when(transactionHandler).confirm(notification, details);

        walletRecreationHandler.confirm(notification, details);

        verify(transactionHandler).confirm(notification, details);
        verify(walletService).swapWallets();
    }

    @Test
    void shouldConfirmButNotSwapWallets() {
        Transaction transaction = new Transaction();
        transaction.setRequiredApprovals(3);
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setPublicKey("pub");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.WALLET_RECREATION);
        transaction.setNotifications(List.of(notification));
        ConfirmationDetails details = new ConfirmationDetails("rawtx", 0L, null);
        doAnswer(a -> {
            notification.setStatus(NotificationStatus.CONFIRMED);
            return null;
        }).when(transactionHandler).confirm(notification, details);

        walletRecreationHandler.confirm(notification, details);

        verify(transactionHandler).confirm(notification, details);
        verify(walletService, never()).swapWallets();
    }

    @Test
    void shouldDenyAndRemoveWalletIfTransactionIsDenied() {
        Transaction transaction = new Transaction();
        transaction.setRequiredApprovals(1);
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setPublicKey("pub");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.WALLET_RECREATION);
        transaction.setNotifications(List.of(notification));
        doAnswer(a -> {
            notification.setStatus(NotificationStatus.DENIED);
            return null;
        }).when(transactionHandler).deny(notification);

        walletRecreationHandler.deny(notification);

        verify(transactionHandler).deny(notification);
        verify(walletService).deleteWalletInCreation();
    }

    @Test
    void shouldDenyButNotDelete() {
        Transaction transaction = new Transaction();
        transaction.setRequiredApprovals(3);
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setPublicKey("pub");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.WALLET_RECREATION);
        transaction.setNotifications(List.of(notification));
        doAnswer(a -> {
            notification.setStatus(NotificationStatus.DENIED);
            return null;
        }).when(transactionHandler).deny(notification);

        walletRecreationHandler.deny(notification);

        verify(transactionHandler).deny(notification);
        verify(walletService, never()).deleteWalletInCreation();
    }

    @Test
    void shouldNotSwapWalletsWhenTransactionIsFailed() {
        Transaction transaction = new Transaction();
        transaction.setRequiredApprovals(1);
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setPublicKey("pub");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.WALLET_RECREATION);
        transaction.setNotifications(List.of(notification));
        ConfirmationDetails details = new ConfirmationDetails("rawtx", 0L, null);
        doAnswer(a -> {
            notification.setStatus(NotificationStatus.CONFIRMED);
            transaction.setStatus(TransactionStatus.FAILED);
            return null;
        }).when(transactionHandler).confirm(notification, details);

        walletRecreationHandler.confirm(notification, details);

        verify(transactionHandler).confirm(notification, details);
        verify(walletService, never()).swapWallets();
        verify(walletService).deleteWalletInCreation();
    }

    @Test
    void shouldReturnCorrectType() {
        NotificationType type = walletRecreationHandler.getType();
        assertThat(type).isEqualTo(NotificationType.WALLET_RECREATION);
    }
}

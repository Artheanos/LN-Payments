package pl.edu.pjatk.lnpayments.webservice.transaction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.service.NotificationService;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionRequest;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BitcoinService bitcoinService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private WalletService walletService;

    @Captor
    private ArgumentCaptor<List<Notification>> notifications;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldCreateTransactionAndSendNotification() {
        TransactionRequest transactionRequest = new TransactionRequest(100L, "addr");
        Wallet wallet = Wallet.builder().address("2137").minSignatures(1).build();
        AdminUser adminUser = createAdminUser("test@test.pl");
        adminUser.setId(1L);
        wallet.setUsers(Collections.singletonList(adminUser));
        Transaction transaction = new Transaction();
        transaction.setId(2L);
        when(walletService.getActiveWallet()).thenReturn(wallet);
        when(transactionRepository.findFirstByStatus(TransactionStatus.PENDING)).thenReturn(Optional.empty());
        when(bitcoinService.createTransaction("2137", "addr", 100L)).thenReturn(transaction);

        transactionService.createTransaction(transactionRequest);

        verify(transactionRepository).save(any());
        verify(notificationService).sendAllNotifications(notifications.capture());
        List<Notification> notificationsValue = notifications.getValue();
        assertThat(notificationsValue).hasSize(1);
        assertThat(notificationsValue.get(0).getAdminUser().getEmail()).isEqualTo("test@test.pl");
        assertThat(notificationsValue.get(0).getType()).isEqualTo(NotificationType.TRANSACTION);
        assertThat(notificationsValue.get(0).getMessage()).isEqualTo("Transaction confirmation");
    }

    @Test
    void shouldThrowExceptionWhenTransactionAlreadyExists() {
        Transaction transaction = new Transaction();
        TransactionRequest transactionRequest = new TransactionRequest(100L, "addr");
        when(transactionRepository.findFirstByStatus(TransactionStatus.PENDING)).thenReturn(Optional.of(transaction));

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> transactionService.createTransaction(transactionRequest))
                .withMessage("Pending transaction already exists!");
    }
}

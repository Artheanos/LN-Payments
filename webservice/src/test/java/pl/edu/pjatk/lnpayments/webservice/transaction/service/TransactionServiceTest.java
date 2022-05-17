package pl.edu.pjatk.lnpayments.webservice.transaction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.resource.NotificationSocketController;
import pl.edu.pjatk.lnpayments.webservice.transaction.converter.TransactionConverter;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionRequest;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionResponse;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BitcoinService bitcoinService;

    @Mock
    private NotificationSocketController notificationSocketController;

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionConverter transactionConverter;

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
        verify(notificationSocketController).sendAllNotifications(notifications.capture());
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

    @Test
    void shouldReturnAllTransactions() {
        Transaction transaction1 = Transaction.builder()
                .rawTransaction("abc")
                .sourceAddress("qwe")
                .targetAddress("asd")
                .requiredApprovals(2)
                .fee(100L)
                .inputValue(100L)
                .build();
        Transaction transaction2 = Transaction.builder()
                .rawTransaction("ewq")
                .sourceAddress("dsa")
                .targetAddress("czx")
                .requiredApprovals(2)
                .fee(101L)
                .inputValue(99L)
                .build();
        transaction2.setStatus(TransactionStatus.APPROVED);
        TransactionDetails details1 = TransactionDetails.builder()
                .dateCreated(transaction1.getDateCreated())
                .targetAddress(transaction1.getTargetAddress())
                .sourceAddress(transaction1.getSourceAddress())
                .value(transaction1.getFee() + transaction1.getInputValue())
                .requiredApprovals(transaction1.getRequiredApprovals())
                .status(transaction1.getStatus())
                .build();
        TransactionDetails details2 = TransactionDetails.builder()
                .dateCreated(transaction2.getDateCreated())
                .targetAddress(transaction2.getTargetAddress())
                .sourceAddress(transaction2.getSourceAddress())
                .value(transaction2.getFee() + transaction1.getInputValue())
                .requiredApprovals(transaction2.getRequiredApprovals())
                .status(transaction2.getStatus())
                .build();
        when(transactionRepository.findFirstByStatus(TransactionStatus.PENDING))
                .thenReturn(Optional.of(transaction1));
        when((transactionRepository.findAllByStatusNot(eq(TransactionStatus.PENDING), eq(null))))
                .thenReturn(new PageImpl<>(Collections.singletonList(transaction2)));
        when(transactionConverter.convertToDto(transaction1)).thenReturn(details1);
        when(transactionConverter.convertAllToDto(any())).thenReturn(new PageImpl<>(Collections.singletonList(details2)));

        TransactionResponse response = transactionService.findTransactions(null);

        assertThat(response.getPendingTransaction()).isEqualTo(details1);
        assertThat(response.getTransactions().getTotalElements()).isEqualTo(1);
        assertThat(response.getTransactions().getContent().get(0)).isEqualTo(details2);
    }

    @Test
    void shouldNotReturnPendingTransactionWhenThereIsNone() {
        Transaction transaction = Transaction.builder()
                .rawTransaction("ewq")
                .sourceAddress("dsa")
                .targetAddress("czx")
                .requiredApprovals(2)
                .fee(101L)
                .inputValue(99L)
                .build();
        transaction.setStatus(TransactionStatus.APPROVED);
        TransactionDetails details = TransactionDetails.builder()
                .dateCreated(transaction.getDateCreated())
                .targetAddress(transaction.getTargetAddress())
                .sourceAddress(transaction.getSourceAddress())
                .value(transaction.getFee() + transaction.getInputValue())
                .requiredApprovals(transaction.getRequiredApprovals())
                .status(transaction.getStatus())
                .build();
        when(transactionRepository.findFirstByStatus(TransactionStatus.PENDING))
                .thenReturn(Optional.empty());
        when((transactionRepository.findAllByStatusNot(eq(TransactionStatus.PENDING), eq(null))))
                .thenReturn(new PageImpl<>(Collections.singletonList(transaction)));
        when(transactionConverter.convertAllToDto(any())).thenReturn(new PageImpl<>(Collections.singletonList(details)));

        TransactionResponse response = transactionService.findTransactions(null);

        verify(transactionConverter, never()).convertToDto(any());
        assertThat(response.getPendingTransaction()).isNull();
        assertThat(response.getTransactions().getTotalElements()).isEqualTo(1);
        assertThat(response.getTransactions().getContent().get(0)).isEqualTo(details);
    }
}

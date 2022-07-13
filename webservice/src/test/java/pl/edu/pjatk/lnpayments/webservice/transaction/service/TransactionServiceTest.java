package pl.edu.pjatk.lnpayments.webservice.transaction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.resource.NotificationSocketController;
import pl.edu.pjatk.lnpayments.webservice.transaction.converter.TransactionConverter;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.NewTransactionDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionResponse;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.exception.BroadcastException;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.BitcoinWalletBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletDataService;

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
    private WalletDataService walletDataService;

    @Mock
    private TransactionConverter transactionConverter;

    @Captor
    private ArgumentCaptor<List<Notification>> notifications;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldCreateTransactionAndSendNotification() {
        Wallet wallet = Wallet.builder().address("2137").minSignatures(1).build();
        AdminUser adminUser = createAdminUser("test@test.pl");
        adminUser.setId(1L);
        wallet.setUsers(Collections.singletonList(adminUser));
        Transaction transaction = new Transaction();
        transaction.setId(2L);
        when(walletDataService.getActiveWallet()).thenReturn(wallet);
        when(transactionRepository.findFirstByStatus(TransactionStatus.PENDING)).thenReturn(Optional.empty());
        when(bitcoinService.createTransaction("2137", "addr", 100L)).thenReturn(transaction);

        transactionService.createTransaction("addr", 100L);

        verify(transactionRepository).save(any());
        verify(notificationSocketController).sendAllNotifications(notifications.capture());
        List<Notification> notificationsValue = notifications.getValue();
        assertThat(notificationsValue).hasSize(1);
        assertThat(notificationsValue.get(0).getAdminUser().getEmail()).isEqualTo("test@test.pl");
        assertThat(notificationsValue.get(0).getType()).isEqualTo(NotificationType.TRANSACTION);
        assertThat(notificationsValue.get(0).getMessage()).isEqualTo("Transaction confirmation");
    }

    @Test
    void shouldCreateTransactionAndSendNotificationForSweep() {
        Wallet wallet = Wallet.builder().address("2137").minSignatures(1).build();
        AdminUser adminUser = createAdminUser("test@test.pl");
        adminUser.setId(1L);
        wallet.setUsers(Collections.singletonList(adminUser));
        Transaction transaction = new Transaction();
        transaction.setId(2L);
        when(walletDataService.getActiveWallet()).thenReturn(wallet);
        when(transactionRepository.findFirstByStatus(TransactionStatus.PENDING)).thenReturn(Optional.empty());
        when(bitcoinService.createTransaction("2137", "addr")).thenReturn(transaction);

        transactionService.createTransaction("addr");

        verify(transactionRepository).save(any());
        verify(notificationSocketController).sendAllNotifications(notifications.capture());
        List<Notification> notificationsValue = notifications.getValue();
        assertThat(notificationsValue).hasSize(1);
        assertThat(notificationsValue.get(0).getAdminUser().getEmail()).isEqualTo("test@test.pl");
        assertThat(notificationsValue.get(0).getType()).isEqualTo(NotificationType.WALLET_RECREATION);
        assertThat(notificationsValue.get(0).getMessage()).isEqualTo("Transaction confirmation");
    }

    @Test
    void shouldThrowExceptionWhenTransactionAlreadyExists() {
        Transaction transaction = new Transaction();
        when(transactionRepository.findFirstByStatus(TransactionStatus.PENDING)).thenReturn(Optional.of(transaction));

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> transactionService.createTransaction("addr", 100L))
                .withMessage("Pending transaction already exists!");
    }

    @Test
    void shouldThrowExceptionForSweepWhenTransactionAlreadyExists() {
        Transaction transaction = new Transaction();
        when(transactionRepository.findFirstByStatus(TransactionStatus.PENDING)).thenReturn(Optional.of(transaction));

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> transactionService.createTransaction("addr"))
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

    @Test
    void shouldBroadcastTransactionAndSetApprovedStatus() {
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);
        Wallet wallet = Wallet.builder().redeemScript("2137").build();
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setRawTransaction("1234");
        Notification notification1 = new Notification(user, transaction, "message2", NotificationType.TRANSACTION);
        notification1.setStatus(NotificationStatus.CONFIRMED);
        Notification notification2 = new Notification(user, transaction, "message3", NotificationType.TRANSACTION);
        Notification notification3 = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        transaction.setNotifications(List.of(notification1, notification2, notification3));
        when(walletDataService.getActiveWallet()).thenReturn(wallet);

        transactionService.broadcastTransaction(transaction);

        verify(bitcoinService).broadcast("1234", "2137");
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
        assertThat(transaction.getNotifications()).extracting(Notification::getStatus).filteredOn(status -> status == NotificationStatus.EXPIRED).hasSize(2);
        assertThat(transaction.getNotifications()).extracting(Notification::getStatus).filteredOn(status -> status == NotificationStatus.CONFIRMED).hasSize(1);
    }

    @Test
    void shouldNotBroadcastAndSetStatusToFailed() {
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);
        Wallet wallet = Wallet.builder().redeemScript("2137").build();
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setRawTransaction("1234");
        Notification notification1 = new Notification(user, transaction, "message2", NotificationType.TRANSACTION);
        notification1.setStatus(NotificationStatus.CONFIRMED);
        Notification notification2 = new Notification(user, transaction, "message3", NotificationType.TRANSACTION);
        Notification notification3 = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        transaction.setNotifications(List.of(notification1, notification2, notification3));
        when(walletDataService.getActiveWallet()).thenReturn(wallet);
        doThrow(BroadcastException.class).when(bitcoinService).broadcast("1234", "2137");

        transactionService.broadcastTransaction(transaction);

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(transaction.getNotifications()).extracting(Notification::getStatus).filteredOn(status -> status == NotificationStatus.EXPIRED).hasSize(2);
        assertThat(transaction.getNotifications()).extracting(Notification::getStatus).filteredOn(status -> status == NotificationStatus.CONFIRMED).hasSize(1);
    }

    @Test
    void shouldDenyTransactionAndExpireAllNotifications() {
        Transaction transaction = new Transaction();
        transaction.setId(2L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);
        transaction.setRawTransaction("1234");
        Notification notification1 = new Notification(user, transaction, "message2", NotificationType.TRANSACTION);
        notification1.setStatus(NotificationStatus.DENIED);
        Notification notification2 = new Notification(user, transaction, "message3", NotificationType.TRANSACTION);
        Notification notification3 = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        transaction.setNotifications(List.of(notification1, notification2, notification3));

        transactionService.denyTransaction(transaction);

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.DENIED);
        assertThat(transaction.getNotifications()).extracting(Notification::getStatus).filteredOn(status -> status == NotificationStatus.EXPIRED).hasSize(2);
        assertThat(transaction.getNotifications()).extracting(Notification::getStatus).filteredOn(status -> status == NotificationStatus.DENIED).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnBooleanIfTransactionIsInProgress(boolean expected) {
        when(transactionRepository.existsByStatus(TransactionStatus.PENDING)).thenReturn(expected);

        boolean inProgress = transactionService.isTransactionInProgress();

        assertThat(inProgress).isEqualTo(expected);
    }

    @Test
    void shouldReturnNewTransactionInfo() {
        BitcoinWalletBalance bitcoinWalletBalance = BitcoinWalletBalance.builder().availableBalance(100L).unconfirmedBalance(100L).build();
        when(bitcoinService.getBalance()).thenReturn(bitcoinWalletBalance);
        when(transactionRepository.existsByStatus(TransactionStatus.PENDING)).thenReturn(true);

        NewTransactionDetails newTransactionDetails = transactionService.getNewTransactionDetails();

        assertThat(newTransactionDetails.getBitcoinWalletBalance()).isEqualTo(bitcoinWalletBalance);
        assertThat(newTransactionDetails.isPendingExists()).isTrue();
    }

}

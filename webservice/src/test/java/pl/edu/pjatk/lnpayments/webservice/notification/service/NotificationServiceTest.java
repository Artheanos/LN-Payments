package pl.edu.pjatk.lnpayments.webservice.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.notification.strategy.NotificationHandler;
import pl.edu.pjatk.lnpayments.webservice.notification.strategy.NotificationHandlerFactory;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletDataService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationHandlerFactory handlerFactory;

    @Mock
    private WalletDataService walletDataService;

    @InjectMocks
    private NotificationService notificationService;

    private Transaction testTransaction;
    private AdminUser testAdminUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setRawTransaction("rawtx");
        testTransaction.setVersion(5L);
        testTransaction.setTargetAddress("123");
        testTransaction.setInputValue(1L);
        testTransaction.setFee(1L);
        testTransaction.setId(1L);
        testAdminUser = createAdminUser("test@test.pl");
        testAdminUser.setId(1L);
        testNotification = new Notification(testAdminUser, testTransaction, "message1", NotificationType.TRANSACTION);
    }

    @Test
    void shouldReturnNotifications() {
        Page<Notification> notifications = new PageImpl<>(List.of(
                new Notification(testAdminUser, testTransaction, "message1", NotificationType.TRANSACTION),
                new Notification(testAdminUser, testTransaction, "message2", NotificationType.TRANSACTION),
                new Notification(testAdminUser, testTransaction, "message3", NotificationType.TRANSACTION)
        ));
        when(notificationRepository.findAllByAdminUserEmail("test@test.pl", Pageable.ofSize(10)))
                .thenReturn(notifications);

        Page<Notification> notificationPage =
                notificationService.getNotificationsByEmail(testAdminUser.getEmail(), Pageable.ofSize(10));

        assertThat(notificationPage).isEqualTo(notifications);
    }

    @Test
    void shouldReturnConfirmationDetails() {
        Wallet wallet = new Wallet();
        wallet.setRedeemScript("redeem");
        when(walletDataService.getActiveWallet()).thenReturn(wallet);
        when(notificationRepository.findByIdentifier("d6b5915c46")).thenReturn(Optional.of(testNotification));

        ConfirmationDetails data = notificationService.getSignatureData(testNotification.getIdentifier());

        assertThat(data.getRawTransaction()).isEqualTo(testTransaction.getRawTransaction());
        assertThat(data.getVersion()).isEqualTo(testTransaction.getVersion());
        assertThat(data.getRedeemScript()).isEqualTo(wallet.getRedeemScript());
    }

    @Test
    void shouldThrowExceptionWhenNotificationWithIdDoesNotExists() {
        when(notificationRepository.findByIdentifier(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> notificationService.getSignatureData("ddd"))
                .withMessage("Notification not found: ddd");
    }

    @Test
    void shouldConfirmTransactionForUser() {
        NotificationHandler handler = mock(NotificationHandler.class);
        when(handlerFactory.findHandler(any())).thenReturn(handler);
        when(notificationRepository.findByIdentifier("d6b5915c46")).thenReturn(Optional.of(testNotification));

        notificationService.handleNotificationResponse("d6b5915c46", null);

        verify(handler).confirm(testNotification, null);
    }

    @Test
    void shouldThrowExceptionWhenExceptionIsFinalizedWhenConfirming() {
        testNotification.setStatus(NotificationStatus.DENIED);
        when(notificationRepository.findByIdentifier("d6b5915c46")).thenReturn(Optional.of(testNotification));

        verify(handlerFactory, never()).findHandler(any());
        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> notificationService.handleNotificationResponse("d6b5915c46", null))
                .withMessage("Notification is already finalized: d6b5915c46");
    }

    @Test
    void shouldDenyTransactionForUser() {
        NotificationHandler handler = mock(NotificationHandler.class);
        when(handlerFactory.findHandler(any())).thenReturn(handler);
        when(notificationRepository.findByIdentifier("d6b5915c46")).thenReturn(Optional.of(testNotification));

        notificationService.handleNotificationDenial("d6b5915c46");

        verify(handler).deny(testNotification);
    }

    @Test
    void shouldThrowExceptionWhenExceptionIsFinalizedWhenDenying() {
        testNotification.setStatus(NotificationStatus.DENIED);
        when(notificationRepository.findByIdentifier("d6b5915c46")).thenReturn(Optional.of(testNotification));

        verify(handlerFactory, never()).findHandler(any());
        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> notificationService.handleNotificationDenial("d6b5915c46"))
                .withMessage("Notification is already finalized: d6b5915c46");
    }
}

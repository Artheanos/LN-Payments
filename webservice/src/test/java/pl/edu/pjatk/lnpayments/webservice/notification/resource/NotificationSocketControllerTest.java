package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.notification.converter.NotificationConverter;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ExtendWith(MockitoExtension.class)
class NotificationSocketControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private NotificationConverter notificationConverter;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationSocketController notificationSocketController;

    @Test
    void shouldSendNotifications() {
        Transaction testTransaction = new Transaction();
        testTransaction.setRawTransaction("rawtx");
        testTransaction.setVersion(5L);
        testTransaction.setTargetAddress("123");
        testTransaction.setInputValue(1L);
        testTransaction.setFee(1L);
        testTransaction.setId(1L);
        AdminUser testAdminUser = createAdminUser("test@test.pl");
        testAdminUser.setId(1L);
        Notification testNotification = new Notification(testAdminUser, testTransaction, "message1", NotificationType.TRANSACTION);
        NotificationResponse response = NotificationResponse.builder().message("message").build();
        when(notificationConverter.convertToDto(testNotification)).thenReturn(response);
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);

        notificationSocketController.sendAllNotifications(List.of(testNotification));

        verify(messagingTemplate).convertAndSend("/topic/268697a5ad", response);
    }
}

package pl.edu.pjatk.lnpayments.webservice.notification.service;

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
import pl.edu.pjatk.lnpayments.webservice.notification.model.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private NotificationConverter notificationConverter;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldSendNotifications() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.TRANSACTION);
        NotificationResponse response = NotificationResponse.builder().message("message").build();
        when(notificationConverter.convertToDto(notification)).thenReturn(response);
        when(notificationRepository.save(notification)).thenReturn(notification);

        notificationService.sendAllNotifications(List.of(notification));

        verify(messagingTemplate).convertAndSend("/topic/268697a5ad", response);
    }
}

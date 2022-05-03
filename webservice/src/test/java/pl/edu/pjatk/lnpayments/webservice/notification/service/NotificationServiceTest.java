package pl.edu.pjatk.lnpayments.webservice.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.notification.converter.NotificationConverter;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.model.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void shouldReturnNotifications() {
        Transaction transaction = new Transaction();
        transaction.setTargetAddress("123");
        transaction.setInputValue(1L);
        transaction.setFee(1L);
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);
        Page<Notification> notifications = new PageImpl<>(List.of(
                new Notification(user, transaction, "message1", NotificationType.TRANSACTION),
                new Notification(user, transaction, "message2", NotificationType.TRANSACTION),
                new Notification(user, transaction, "message3", NotificationType.TRANSACTION)
        ));
        when(notificationRepository.findAllByAdminUserEmail("test@test.pl", Pageable.ofSize(10)))
                .thenReturn(notifications);

        Page<Notification> notificationPage = notificationService.getNotificationsByEmail(user.getEmail(), Pageable.ofSize(10));

        assertThat(notificationPage).isEqualTo(notifications);
    }
}

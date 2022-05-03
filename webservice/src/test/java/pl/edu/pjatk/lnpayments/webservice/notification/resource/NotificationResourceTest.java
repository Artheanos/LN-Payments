package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.notification.converter.NotificationConverter;
import pl.edu.pjatk.lnpayments.webservice.notification.service.NotificationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationResourceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationConverter notificationConverter;

    @InjectMocks
    private NotificationResource notificationResource;

    @Test
    void shouldReturnOkForAllNotifications() {
        ResponseEntity<?> notifications = notificationResource.getNotificationsByUser(() -> "test", null);

        assertThat(notifications.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(notificationService).getNotificationsByEmail("test", null);
    }
}

package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.notification.converter.NotificationConverter;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
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

    @Nested
    class AllNotifications {
        @Test
        void shouldReturnOk() {
            ResponseEntity<?> notifications = notificationResource.getNotificationsByUser(() -> "test", null);

            assertThat(notifications.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(notificationService).getNotificationsByEmail("test", null);
        }
    }

    @Nested
    class ConfirmationDataRequest {
        @Test
        void shouldReturnOk() {
            ResponseEntity<?> notifications = notificationResource.getSignatureRequiredData("ddd");

            assertThat(notifications.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(notificationService).getSignatureData("ddd");
        }
    }

    @Nested
    class ConfirmationRequest {
        @Test
        void shouldReturnOk() {
            ConfirmationDetails details = new ConfirmationDetails("ddd", 1L, null);

            ResponseEntity<?> response = notificationResource.confirmNotification("aaa", details);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(notificationService).handleNotificationResponse("aaa", details);
        }
    }

    @Nested
    class DenialRequest {
        @Test
        void shouldReturnOk() {
            ResponseEntity<?> response = notificationResource.denyNotification("aaa");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(notificationService).handleNotificationDenial("aaa");
        }
    }

    @Nested
    class GetNotification {
        @Test
        void shouldReturnOk() {
            ResponseEntity<?> response = notificationResource.getNotification("123");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(notificationService).findNotification("123");
        }
    }
}

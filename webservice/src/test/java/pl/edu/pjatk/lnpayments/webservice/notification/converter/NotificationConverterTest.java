package pl.edu.pjatk.lnpayments.webservice.notification.converter;

import org.junit.jupiter.api.Test;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.model.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

class NotificationConverterTest {

    private final NotificationConverter notificationService = new NotificationConverter();

    @Test
    void shouldConvertToDto() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.TRANSACTION);

        NotificationResponse response = notificationService.convertToDto(notification);

        assertThat(response.getMessage()).isEqualTo("message");
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(response.getType()).isEqualTo(NotificationType.TRANSACTION);
        assertThat(response.getAmount()).isEqualTo(null);
        assertThat(response.getAddress()).isEqualTo(null);
    }
}

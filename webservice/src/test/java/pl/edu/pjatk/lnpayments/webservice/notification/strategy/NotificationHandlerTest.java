package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import org.junit.jupiter.api.Test;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

class NotificationHandlerTest {

    @Test
    void shouldChangeStatusToDenied() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.TRANSACTION);
        NotificationHandler handler = new NotificationHandler() {
            @Override
            public void confirm(Notification notification, ConfirmationDetails data) {

            }

            @Override
            public NotificationType getType() {
                return null;
            }
        };

        handler.deny(notification);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.DENIED);
    }

}

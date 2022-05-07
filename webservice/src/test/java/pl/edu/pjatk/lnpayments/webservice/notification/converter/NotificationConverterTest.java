package pl.edu.pjatk.lnpayments.webservice.notification.converter;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

class NotificationConverterTest {

    private final NotificationConverter notificationConverter = new NotificationConverter();

    @Test
    void shouldConvertToDto() {
        Transaction transaction = new Transaction();
        transaction.setFee(12L);
        transaction.setInputValue(16L);
        transaction.setTargetAddress("ddd");
        transaction.setId(1L);
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);
        Notification notification = new Notification(user, transaction, "message", NotificationType.TRANSACTION);

        NotificationResponse response = notificationConverter.convertToDto(notification);

        assertThat(response.getMessage()).isEqualTo("message");
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(response.getType()).isEqualTo(NotificationType.TRANSACTION);
        assertThat(response.getAmount()).isEqualTo(28L);
        assertThat(response.getAddress()).isEqualTo("ddd");
        assertThat(response.getId()).isEqualTo("d6b5915c46");
    }

    @Test
    void shouldConvertAllToDto() {
        Transaction transaction1 = new Transaction();
        transaction1.setFee(12L);
        transaction1.setInputValue(16L);
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction();
        transaction2.setFee(12L);
        transaction2.setInputValue(16L);
        transaction2.setId(2L);
        AdminUser user = createAdminUser("test1@test.pl");
        user.setId(1L);
        Notification notification1 = new Notification(user, transaction1, "message1", NotificationType.TRANSACTION);
        Notification notification2 = new Notification(user, transaction2, "message2", NotificationType.TRANSACTION);
        Page<Notification> notificationPage = new PageImpl<>(List.of(notification1, notification2));

        Page<NotificationResponse> responses = notificationConverter.convertAllToDto(notificationPage);

        assertThat(responses.getTotalElements()).isEqualTo(2);
        assertThat(responses.getContent())
                .extracting(NotificationResponse::getMessage)
                .containsAll(List.of("message1", "message2"));

    }
}

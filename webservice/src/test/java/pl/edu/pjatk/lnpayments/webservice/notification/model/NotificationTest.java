package pl.edu.pjatk.lnpayments.webservice.notification.model;

import org.junit.jupiter.api.Test;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

class NotificationTest {

    @Test
    void shouldCalculateProperMessageChannelId() {
        AdminUser user = createAdminUser("test@test.pl");
        user.setId(1L);

        String channelId = user.notificationsChannelId();

        assertThat(channelId).isEqualTo("268697a5ad");
    }
}

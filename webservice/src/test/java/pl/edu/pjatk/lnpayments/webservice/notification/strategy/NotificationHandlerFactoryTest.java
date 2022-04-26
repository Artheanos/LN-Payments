package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import org.junit.jupiter.api.Test;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationHandlerFactoryTest {

    @Test
    void shouldReturnRightHandler() {
        NotificationHandler handler = mock(NotificationHandler.class);
        when(handler.getType()).thenReturn(NotificationType.TRANSACTION);
        NotificationHandlerFactory factory = new NotificationHandlerFactory(Set.of(handler));

        NotificationHandler notificationHandler = factory.findHandler(NotificationType.TRANSACTION);

        assertThat(notificationHandler).isEqualTo(handler);
    }
}

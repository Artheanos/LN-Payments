package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Service
public class NotificationHandlerFactory {

    private final Map<NotificationType, NotificationHandler> handlers = new EnumMap<>(NotificationType.class);

    @Autowired
    NotificationHandlerFactory(Set<NotificationHandler> handlerSet) {
        createHandlers(handlerSet);
    }

    public NotificationHandler findHandler(NotificationType notificationType) {
        return handlers.get(notificationType);
    }

    private void createHandlers(Set<NotificationHandler> handlerSet) {
        handlerSet.forEach(handler -> handlers.put(handler.getType(), handler));
    }
}

package pl.edu.pjatk.lnpayments.webservice.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.notification.converter.NotificationConverter;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.notification.strategy.NotificationHandler;
import pl.edu.pjatk.lnpayments.webservice.notification.strategy.NotificationHandlerFactory;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

import javax.transaction.Transactional;

@Controller
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationHandlerFactory handlerFactory;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate,
                               NotificationConverter notificationConverter,
                               NotificationRepository notificationRepository,
                               NotificationHandlerFactory handlerFactory) {
        this.notificationRepository = notificationRepository;
        this.handlerFactory = handlerFactory;
    }

    public Page<Notification> getNotificationsByEmail(String name, Pageable pageable) {
        return notificationRepository.findAllByAdminUserEmail(name, pageable);
    }

    public ConfirmationDetails getSignatureData(String id) {
        Notification notification = findNotification(id);
        Transaction transaction = notification.getTransaction();
        String rawTransaction = transaction.getRawTransaction();
        long version = transaction.getVersion();
        return new ConfirmationDetails(rawTransaction, version);
    }

    @Transactional
    public void handleNotificationResponse(String id, ConfirmationDetails body) {
        Notification notification = findNotification(id);
        validateUnhandledNotification(id, notification);
        NotificationHandler handler = handlerFactory.findHandler(notification.getType());
        handler.confirm(notification, body);
    }

    @Transactional
    public void handleNotificationDenial(String id) {
        Notification notification = findNotification(id);
        validateUnhandledNotification(id, notification);
        NotificationHandler handler = handlerFactory.findHandler(notification.getType());
        handler.deny(notification);
    }

    private Notification findNotification(String id) {
        return notificationRepository.findByIdentifier(id)
                .orElseThrow(() -> new NotFoundException("Notification not found: " + id));
    }

    private void validateUnhandledNotification(String id, Notification notification) {
        if (notification.isFinalized())
            throw new InconsistentDataException("Notification is already finalized: " + id);
    }
}

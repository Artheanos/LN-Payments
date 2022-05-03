package pl.edu.pjatk.lnpayments.webservice.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.notification.converter.NotificationConverter;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;

import java.util.List;

@Controller
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationConverter notificationConverter;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate,
                               NotificationConverter notificationConverter,
                               NotificationRepository notificationRepository) {
        this.messagingTemplate = messagingTemplate;
        this.notificationConverter = notificationConverter;
        this.notificationRepository = notificationRepository;
    }

    public void sendAllNotifications(List<Notification> notifications) {
        notifications.forEach(this::sendNotification);
    }

    private void sendNotification(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        AdminUser user = savedNotification.getAdminUser();
        this.messagingTemplate.convertAndSend("/topic/" + user.notificationsChannelId(),
                notificationConverter.convertToDto(notification));
    }

    public Page<Notification> getNotificationsByEmail(String name, Pageable pageable) {
        return notificationRepository.findAllByAdminUserEmail(name, pageable);
    }
}

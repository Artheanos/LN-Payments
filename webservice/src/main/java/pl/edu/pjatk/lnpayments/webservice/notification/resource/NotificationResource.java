package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pjatk.lnpayments.webservice.notification.converter.NotificationConverter;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.notification.service.NotificationService;

import java.security.Principal;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.NOTIFICATIONS_PATH;

@RestController
@RequestMapping(NOTIFICATIONS_PATH)
class NotificationResource {

    private final NotificationService notificationService;
    private final NotificationConverter notificationConverter;

    @Autowired
    NotificationResource(NotificationService notificationService, NotificationConverter notificationConverter) {
        this.notificationService = notificationService;
        this.notificationConverter = notificationConverter;
    }

    @GetMapping
    ResponseEntity<Page<NotificationResponse>> getNotificationsByUser(Principal principal, Pageable pageable) {
        Page<Notification> notifications = notificationService.getNotificationsByEmail(principal.getName(), pageable);
        Page<NotificationResponse> notificationResponses = notificationConverter.convertAllToDto(notifications);
        return ResponseEntity.ok(notificationResponses);
    }
}

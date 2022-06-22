package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.notification.converter.NotificationConverter;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification_;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.notification.service.NotificationService;

import javax.validation.Valid;
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
    ResponseEntity<Page<NotificationResponse>> getNotificationsByUser(
            Principal principal,
            @SortDefault(sort = Notification_.DATE, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Notification> notifications = notificationService.getNotificationsByEmail(principal.getName(), pageable);
        Page<NotificationResponse> notificationResponses = notificationConverter.convertAllToDto(notifications);
        return ResponseEntity.ok(notificationResponses);
    }

    @GetMapping("{id}")
    ResponseEntity<NotificationResponse> getNotification(@PathVariable String id) {
        Notification notification = notificationService.findNotification(id);
        NotificationResponse response = notificationConverter.convertToDto(notification);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}/transaction")
    ResponseEntity<ConfirmationDetails> getSignatureRequiredData(@PathVariable String id) {
        ConfirmationDetails details = notificationService.getSignatureData(id);
        return ResponseEntity.ok(details);
    }

    @PostMapping("{id}/confirm")
    ResponseEntity<?> confirmNotification(@PathVariable String id, @Valid @RequestBody ConfirmationDetails body) {
        notificationService.handleNotificationResponse(id, body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/deny")
    ResponseEntity<?> denyNotification(@PathVariable String id) {
        notificationService.handleNotificationDenial(id);
        return ResponseEntity.ok().build();
    }

}

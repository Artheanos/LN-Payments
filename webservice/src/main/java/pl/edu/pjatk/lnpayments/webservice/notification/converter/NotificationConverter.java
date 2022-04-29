package pl.edu.pjatk.lnpayments.webservice.notification.converter;

import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.notification.model.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;

@Service
public class NotificationConverter {

    public NotificationResponse convertToDto(Notification notification) {
        return NotificationResponse.builder()
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .build();
    }

}

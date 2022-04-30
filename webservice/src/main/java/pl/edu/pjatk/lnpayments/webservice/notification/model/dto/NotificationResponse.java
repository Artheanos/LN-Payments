package pl.edu.pjatk.lnpayments.webservice.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String message;
    private NotificationStatus status;
    private NotificationType type;
    private Long amount;
    private String address;
}

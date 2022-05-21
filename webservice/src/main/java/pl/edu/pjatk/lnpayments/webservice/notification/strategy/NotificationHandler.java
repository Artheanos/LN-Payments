package pl.edu.pjatk.lnpayments.webservice.notification.strategy;

import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;

public interface NotificationHandler {

    void confirm(Notification notification, ConfirmationDetails data);

    void deny(Notification notification);

    NotificationType getType();
}

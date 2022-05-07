package pl.edu.pjatk.lnpayments.webservice.notification.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

@Service
public class NotificationConverter {

    public NotificationResponse convertToDto(Notification notification) {
        Transaction transaction = notification.getTransaction();
        return NotificationResponse.builder()
                .id(notification.getIdentifier())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .address(transaction.getTargetAddress())
                .amount(transaction.getInputValue() + transaction.getFee())
                .build();
    }

    public Page<NotificationResponse> convertAllToDto(Page<Notification> notifications) {
        return notifications.map(this::convertToDto);
    }

}

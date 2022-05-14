package pl.edu.pjatk.lnpayments.webservice.notification.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Notification {

    @EmbeddedId
    private NotificationId notificationId;

    @ManyToOne
    @MapsId("userId")
    private AdminUser adminUser;

    @ManyToOne
    @MapsId("transactionId")
    private Transaction transaction;

    private String identifier;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Instant date;

    public Notification(AdminUser adminUser,
                        Transaction transaction,
                        String message,
                        NotificationType notificationType) {
        this.notificationId = new NotificationId(adminUser.getId(), transaction.getId());
        this.identifier = DigestUtils.sha256Hex(adminUser.getId() + ":" + transaction.getId()).substring(0, 10);
        this.adminUser = adminUser;
        this.transaction = transaction;
        this.message = message;
        this.type = notificationType;
        this.status = NotificationStatus.PENDING;
        this.date = Instant.now();
    }

    public boolean isFinalized() {
        return status != NotificationStatus.PENDING;
    }
}

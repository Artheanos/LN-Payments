package pl.edu.pjatk.lnpayments.webservice.transaction.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4096)
    private String rawTransaction;
    private String sourceAddress;
    private String targetAddress;
    private Long inputValue;
    private Long fee;
    private Integer requiredApprovals;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private Instant dateCreated;

    @Version
    private long version;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.EAGER)
    private List<Notification> notifications;

    @Builder
    public Transaction(String rawTransaction,
                       String sourceAddress,
                       String targetAddress,
                       Long inputValue,
                       Long fee,
                       Integer requiredApprovals) {
        this.rawTransaction = rawTransaction;
        this.sourceAddress = sourceAddress;
        this.targetAddress = targetAddress;
        this.inputValue = inputValue;
        this.fee = fee;
        this.requiredApprovals = requiredApprovals;
        this.status = TransactionStatus.PENDING;
        this.dateCreated = Instant.now();
    }

    public boolean isConfirmed() {
        return requiredApprovals <= countNotifications(NotificationStatus.CONFIRMED);
    }

    public boolean isDenied() {
        return requiredApprovals <= countNotifications(NotificationStatus.DENIED);
    }

    private long countNotifications(NotificationStatus status) {
        return notifications.stream()
                .filter(notification -> notification.getStatus() == status)
                .count();
    }
}

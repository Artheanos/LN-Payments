package pl.edu.pjatk.lnpayments.webservice.transaction.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String rawTransaction;
    private String sourceAddress;
    private String targetAddress;
    private Long inputValue;
    private Long fee;
    private Integer requiredApprovals;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

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
    }
}

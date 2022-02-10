package pl.edu.pjatk.lnpayments.webservice.payment.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 512)
    private String paymentRequest;
    private Instant date;
    private Instant expiry;
    private PaymentStatus status;

    public Payment(String paymentRequest, int expiryInSeconds, PaymentStatus paymentStatus) {
        this.paymentRequest = paymentRequest;
        this.date = Instant.now();
        this.expiry = date.plusSeconds(expiryInSeconds);
        this.status = paymentStatus;
    }

}

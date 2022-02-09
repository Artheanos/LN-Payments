package pl.edu.pjatk.lnpayments.webservice.payment.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentRequest;
    private Instant date;
    private Instant expiry;
    private PaymentStatus status;

    public Payment(String paymentRequest, int expiryInSeconds) {
        this.paymentRequest = paymentRequest;
        this.date = Instant.now();
        this.expiry = date.plusSeconds(expiryInSeconds);
    }

    public Payment() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(String paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }
}

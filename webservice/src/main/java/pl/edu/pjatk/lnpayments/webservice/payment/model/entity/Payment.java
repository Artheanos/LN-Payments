package pl.edu.pjatk.lnpayments.webservice.payment.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

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
    private int numberOfTokens;
    private int price;
    private Instant date;
    private Instant expiry;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private final Collection<Token> tokens = new HashSet<>();

    public Payment(String paymentRequest,
                   int numberOfTokens,
                   int price,
                   int expiryInSeconds,
                   PaymentStatus paymentStatus) {
        this.paymentRequest = paymentRequest;
        this.numberOfTokens = numberOfTokens;
        this.price = price;
        this.date = Instant.now();
        this.expiry = date.plusSeconds(expiryInSeconds);
        this.status = paymentStatus;
    }

    public void assignTokens(Collection<Token> tokens) {
        this.tokens.addAll(tokens);
    }
}

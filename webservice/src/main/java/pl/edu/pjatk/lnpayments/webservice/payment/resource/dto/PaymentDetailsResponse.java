package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.Builder;
import lombok.Getter;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;

import java.time.Instant;
import java.util.Collection;

@Getter
@Builder
public class PaymentDetailsResponse {

    private String paymentRequest;
    private String paymentTopic;
    private Instant timestamp;
    private Instant expirationTimestamp;
    private int price;
    private int numberOfTokens;
    private PaymentStatus paymentStatus;
    private Collection<String> tokens;

}

package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class PaymentDetailsResponse {
    public PaymentDetailsResponse(String paymentRequest, Instant timestamp, Instant expirationTimestamp) {
        this.paymentRequest = paymentRequest;
        this.timestamp = timestamp;
        this.expirationTimestamp = expirationTimestamp;
    }

    private String paymentRequest;
    private Instant timestamp;
    private Instant expirationTimestamp;

}

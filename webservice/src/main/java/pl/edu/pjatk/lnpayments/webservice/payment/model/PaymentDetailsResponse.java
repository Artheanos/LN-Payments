package pl.edu.pjatk.lnpayments.webservice.payment.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class PaymentDetailsResponse {

    private String paymentRequest;
    private Instant timestamp;
    private Instant expirationTimestamp;
    private String nodeUrl;

}

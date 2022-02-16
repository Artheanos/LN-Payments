package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class PaymentDetailsResponse {

    private String paymentRequest;
    private Instant timestamp;
    private Instant expirationTimestamp;

}

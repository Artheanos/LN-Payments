package pl.edu.pjatk.lnpayments.webservice.payment.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PaymentDetailsResponse {

    private String paymentRequest;
    private Instant timestamp;
    private Instant expirationTimestamp;
    private String nodeUrl;

}

package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.Builder;
import lombok.Getter;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;

import java.time.Instant;

@Getter
@Builder
public class PaymentAdminDetailsResponse {

    private String email;
    private Instant timestamp;
    private int price;
    private int numberOfTokens;
    private PaymentStatus paymentStatus;

}

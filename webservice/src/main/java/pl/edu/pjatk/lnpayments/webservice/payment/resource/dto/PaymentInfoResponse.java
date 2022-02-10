package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaymentInfoResponse {

    private int price;
    private String description;
    private String nodeUrl;
    private List<PaymentDetailsResponse> pendingPayments;
}

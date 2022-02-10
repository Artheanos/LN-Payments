package pl.edu.pjatk.lnpayments.webservice.payment.model;

import lombok.Builder;
import lombok.Getter;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;

import java.util.List;

@Getter
@Builder
public class PaymentInfo {

    private int price;
    private String description;
    private String nodeUrl;
    private List<Payment> pendingPayments;
}

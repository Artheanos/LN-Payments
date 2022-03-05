package pl.edu.pjatk.lnpayments.webservice.payment.model;

import lombok.Builder;
import lombok.Getter;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;

import java.util.Collection;

@Getter
@Builder
public class PaymentInfo {

    private int price;
    private String description;
    private String nodeUrl;
    private Collection<Payment> pendingPayments;
}

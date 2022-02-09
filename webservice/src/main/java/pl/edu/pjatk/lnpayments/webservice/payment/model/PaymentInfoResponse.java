package pl.edu.pjatk.lnpayments.webservice.payment.model;

import lombok.Data;

import java.util.List;

@Data
public class PaymentInfoResponse {

    private int price;
    private String description;
    private List<PendingPayment> pendingPayments;
}

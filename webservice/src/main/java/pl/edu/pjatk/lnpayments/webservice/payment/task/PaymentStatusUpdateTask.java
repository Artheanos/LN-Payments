package pl.edu.pjatk.lnpayments.webservice.payment.task;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.service.PaymentDataService;

@Slf4j
public class PaymentStatusUpdateTask implements Runnable {

    private final Payment payment;
    private final PaymentDataService paymentDataService;

    public PaymentStatusUpdateTask(Payment payment, PaymentDataService paymentDataService) {
        this.payment = payment;
        this.paymentDataService = paymentDataService;
    }

    @Override
    public void run() {
        Payment payment = paymentDataService.findPaymentByRequest(this.payment.getPaymentRequest());
        if (payment.getStatus() != PaymentStatus.COMPLETE) {
            payment.setStatus(PaymentStatus.CANCELLED);
            paymentDataService.savePayment(payment);
        }
    }
}

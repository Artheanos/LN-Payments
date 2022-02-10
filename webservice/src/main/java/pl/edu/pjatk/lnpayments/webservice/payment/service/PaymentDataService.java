package pl.edu.pjatk.lnpayments.webservice.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.PaymentRepository;

import java.util.Collections;
import java.util.List;

@Service
public class PaymentDataService {

    private final PaymentRepository paymentRepository;

    @Autowired
    PaymentDataService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> findPendingPaymentsByUser() {
        //TODO implement, when we have a way to authenticate users
        return Collections.emptyList();
    }
}

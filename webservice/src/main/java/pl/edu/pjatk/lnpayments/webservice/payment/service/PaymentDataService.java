package pl.edu.pjatk.lnpayments.webservice.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.PaymentRepository;

import java.util.Collection;

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

    public Collection<Payment> findPendingPaymentsByUser(String email) {
        return paymentRepository.findPendingPaymentsByUserEmailAndStatus(email, PaymentStatus.PENDING);
    }

    public Payment findPaymentByRequest(String paymentRequest) {
        return paymentRepository.findPaymentByPaymentRequest(paymentRequest)
                .orElseThrow(InconsistentDataException::new);
    }

}

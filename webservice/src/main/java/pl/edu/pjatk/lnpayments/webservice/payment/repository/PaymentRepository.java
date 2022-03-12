package pl.edu.pjatk.lnpayments.webservice.payment.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findPaymentByPaymentRequest(String paymentRequest);

    Collection<Payment> findPendingPaymentsByUserEmailAndStatus(String email, PaymentStatus status);
}

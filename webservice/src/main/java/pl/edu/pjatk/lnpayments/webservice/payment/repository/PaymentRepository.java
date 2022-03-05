package pl.edu.pjatk.lnpayments.webservice.payment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Optional<Payment> findPaymentByPaymentRequest(String paymentRequest);

    Collection<Payment> findPendingPaymentsByUserEmailAndStatus(@Param("email") String email,
                                                                @Param("status") PaymentStatus status);
}

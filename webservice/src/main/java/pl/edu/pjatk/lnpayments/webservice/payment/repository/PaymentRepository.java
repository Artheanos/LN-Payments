package pl.edu.pjatk.lnpayments.webservice.payment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
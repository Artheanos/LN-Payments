package pl.edu.pjatk.lnpayments.webservice.transaction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByStatus(TransactionStatus status);

    Optional<Transaction> findFirstByStatus(TransactionStatus status);

    Page<Transaction> findAllByStatusNot(TransactionStatus status, Pageable pageable);
}

package pl.edu.pjatk.lnpayments.webservice.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByAdminUserEmail(String email, Pageable pageable);

    Optional<Notification> findByIdentifier(String identifier);
}

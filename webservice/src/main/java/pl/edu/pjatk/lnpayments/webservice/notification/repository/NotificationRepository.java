package pl.edu.pjatk.lnpayments.webservice.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

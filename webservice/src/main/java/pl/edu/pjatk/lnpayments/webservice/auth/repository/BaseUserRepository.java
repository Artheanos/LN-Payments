package pl.edu.pjatk.lnpayments.webservice.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;

import java.util.Optional;

@NoRepositoryBean
public interface BaseUserRepository<T extends User> extends JpaRepository<T, Long> {

    Optional<T> findByEmail(String email);

    boolean existsByEmail(String email);
}

package pl.edu.pjatk.lnpayments.webservice.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;

import java.util.Optional;

@NoRepositoryBean
public interface BaseUserRepository<T extends User> extends CrudRepository<T, Long> {

    Optional<T> findByEmail(String email);

    boolean existsByEmail(String email);
}

package pl.edu.pjatk.lnpayments.webservice.auth.repository;

import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;

@Repository
public interface UserRepository extends BaseUserRepository<User> {
}

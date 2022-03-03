package pl.edu.pjatk.lnpayments.webservice.auth.repository;

import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;

@Repository
public interface StandardUserRepository extends BaseUserRepository<StandardUser> {
}

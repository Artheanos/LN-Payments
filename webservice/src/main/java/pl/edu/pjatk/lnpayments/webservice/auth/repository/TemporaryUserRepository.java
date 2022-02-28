package pl.edu.pjatk.lnpayments.webservice.auth.repository;

import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.TemporaryUser;

@Repository
public interface TemporaryUserRepository extends BaseUserRepository<TemporaryUser> {
}

package pl.edu.pjatk.lnpayments.webservice.auth.repository;

import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;

import java.util.List;

@Repository
public interface AdminUserRepository extends BaseUserRepository<AdminUser> {

    List<AdminUser> findAllByEmailInAndPublicKeyNotNull(List<String> emails);

}

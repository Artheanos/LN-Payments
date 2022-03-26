package pl.edu.pjatk.lnpayments.webservice.auth.repository;

import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;

@Repository
public interface AdminUserRepository extends BaseUserRepository<AdminUser> {

}

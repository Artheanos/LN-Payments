package pl.edu.pjatk.lnpayments.webservice.helper.factory;

import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;

public class UserFactory {

    private UserFactory() {
    }

    public static User createUser(String email) {
        return new User(email) {
            @Override
            public Role getRole() {
                return Role.ROLE_USER;
            }
        };
    }

    public static User createStandardUser(String email) {
        return new StandardUser(email, "asd", "asd");
    }

    public static AdminUser createAdminUser(String email) {
        return new AdminUser(email, "asd", "asd");
    }
}

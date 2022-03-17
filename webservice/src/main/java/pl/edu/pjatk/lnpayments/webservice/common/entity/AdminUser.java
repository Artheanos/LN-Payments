package pl.edu.pjatk.lnpayments.webservice.common.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AdminUser extends StandardUser {

    @Builder(builderMethodName = "adminBuilder")
    public AdminUser(String email, String fullName, String password) {
        super(email, fullName, password);
    }

    @Override
    public Role getRole() {
        return Role.ROLE_ADMIN;
    }
}

package pl.edu.pjatk.lnpayments.webservice.common.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
public class AdminUser extends StandardUser {

    @Builder(builderMethodName = "adminBuilder")
    public AdminUser(String email, String fullName, String password) {
        super(email, fullName, password);
    }

    private String publicKey;

    public boolean hasKey() {
        return publicKey != null;
    }

    @Override
    public Role getRole() {
        return Role.ROLE_ADMIN;
    }
}

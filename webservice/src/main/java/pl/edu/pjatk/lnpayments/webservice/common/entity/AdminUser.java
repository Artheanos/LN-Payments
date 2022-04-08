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

    //TODO replace with real one, when implementing endpoint for uploading keys
    private String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";

    @Override
    public Role getRole() {
        return Role.ROLE_ADMIN;
    }
}

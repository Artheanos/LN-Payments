package pl.edu.pjatk.lnpayments.webservice.common.entity;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class TemporaryUser extends User {

    public TemporaryUser(String email) {
        super(email);
    }

    @Override
    public Role getRole() {
        return Role.ROLE_TEMPORARY;
    }

}

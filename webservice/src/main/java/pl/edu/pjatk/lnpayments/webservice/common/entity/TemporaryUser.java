package pl.edu.pjatk.lnpayments.webservice.common.entity;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class TemporaryUser extends User {

    private static final String DELIMITER = "#";

    public TemporaryUser(String email, String hashId) {
        super(email + DELIMITER + hashId);
    }

    @Override
    public Role getRole() {
        return Role.ROLE_TEMPORARY;
    }

}

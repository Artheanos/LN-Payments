package pl.edu.pjatk.lnpayments.webservice.common.entity;

import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class TemporaryUser extends User {

    private static final String DELIMITER = "#";

    public TemporaryUser(String email) {
        this.email = email + DELIMITER + generateHashId();
    }

    @Override
    public Role getRole() {
        return Role.ROLE_TEMPORARY;
    }

    private String generateHashId() {
        return DigestUtils.sha256Hex(LocalDateTime.now().toString()).substring(0, 8);
    }
}

package pl.edu.pjatk.lnpayments.webservice.common.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StandardUser extends User {

    private String fullName;
    private String password;
    private Instant createdAt;

    @Builder
    public StandardUser(String email, String fullName, String password) {
        super(email);
        this.fullName = fullName;
        this.password = password;
        this.createdAt = Instant.now();
    }

    @Override
    public Role getRole() {
        return Role.ROLE_USER;
    }
}

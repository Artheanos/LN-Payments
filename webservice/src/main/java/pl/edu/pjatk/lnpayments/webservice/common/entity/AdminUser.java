package pl.edu.pjatk.lnpayments.webservice.common.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.DynamicUpdate;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;

import javax.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    public boolean hasKey() {
        return publicKey != null;
    }

    public boolean isAssignedToWallet() {
        return wallet != null;
    }

    public String notificationsChannelId() {
        return DigestUtils.sha256Hex(email + this.getId()).substring(0, 10);
    }

    @Override
    public Role getRole() {
        return Role.ROLE_ADMIN;
    }
}

package pl.edu.pjatk.lnpayments.webservice.wallet.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String redeemScript;

    private String scriptPubKey;

    private String address;

    private Integer minSignatures;

    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy = "wallet")
    private List<AdminUser> users;

    @Builder
    public Wallet(String redeemScript,
                  String scriptPubKey,
                  String address,
                  List<AdminUser> users,
                  Integer minSignatures) {
        this.redeemScript = redeemScript;
        this.scriptPubKey = scriptPubKey;
        this.address = address;
        this.users = users;
        this.minSignatures = minSignatures;
    }
}

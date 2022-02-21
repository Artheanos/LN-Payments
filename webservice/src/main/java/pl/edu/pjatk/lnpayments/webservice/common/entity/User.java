package pl.edu.pjatk.lnpayments.webservice.common.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`user`", schema = "public")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String fullName;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String email, String fullName, String password, Role role) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
    }

}

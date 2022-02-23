package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String email;
    private String fullName;
    private Role role;
    private String token;

}

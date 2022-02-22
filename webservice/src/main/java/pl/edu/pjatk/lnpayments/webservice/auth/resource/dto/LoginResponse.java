package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String email;
    private String fullName;
    private Role role;

}

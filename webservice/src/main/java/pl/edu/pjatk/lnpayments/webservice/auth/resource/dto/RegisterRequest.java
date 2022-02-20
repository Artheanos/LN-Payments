package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Pattern(regexp = "^\\S+@\\S+$")
    private String email;
    @Pattern(regexp = "^.*[\\S].{0,100}$")
    private String fullName;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private String password;

}

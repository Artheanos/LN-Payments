package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.FULLNAME_REGEX;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.PASSWORD_REGEX;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Pattern(regexp = "^\\S+@\\S+$")
    private String email;
    @Pattern(regexp = FULLNAME_REGEX)
    private String fullName;
    @Pattern(regexp = PASSWORD_REGEX)
    private String password;

}

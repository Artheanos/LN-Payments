package pl.edu.pjatk.lnpayments.webservice.common.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.PASSWORD_REGEX;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {

    @NotBlank
    private String currentPassword;
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX)
    private String newPassword;
}

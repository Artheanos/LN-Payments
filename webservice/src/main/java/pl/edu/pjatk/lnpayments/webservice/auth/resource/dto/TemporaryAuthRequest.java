package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryAuthRequest {

    @Email
    private String email;
}

package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TemporaryAuthResponse {

    private final String email;
    private final String token;
}

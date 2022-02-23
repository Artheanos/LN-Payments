package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class RefreshTokenResponse {
    private String token;
}

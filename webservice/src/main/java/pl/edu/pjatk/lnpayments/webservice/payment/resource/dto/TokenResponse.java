package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private Collection<String> tokens;
}

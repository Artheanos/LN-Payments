package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class TokenResponse {

    private Collection<String> tokens;
}

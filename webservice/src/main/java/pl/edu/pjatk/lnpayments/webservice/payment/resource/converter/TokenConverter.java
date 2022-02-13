package pl.edu.pjatk.lnpayments.webservice.payment.resource.converter;

import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.TokenResponse;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class TokenConverter {

    public TokenResponse convertToDto(Token token) {
        return new TokenResponse(token.getValue());
    }

    public Collection<TokenResponse> convertAllToDto(Collection<Token> tokens) {
        return tokens.stream().map(this::convertToDto).collect(Collectors.toSet());
    }
}

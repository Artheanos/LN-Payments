package pl.edu.pjatk.lnpayments.webservice.payment.resource.converter;

import org.junit.jupiter.api.Test;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.TokenResponse;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TokenConverterTest {

    private final TokenConverter tokenConverter = new TokenConverter();

    @Test
    void shouldConvertToDto() {
        Token token1 = new Token("token1");
        Token token2 = new Token("token2");
        Set<Token> tokens = Set.of(token1, token2);

        TokenResponse tokenResponse = tokenConverter.convertAllToDto(tokens);

        assertThat(tokenResponse.getTokens()).hasSize(2);
        assertThat(tokenResponse.getTokens()).contains(token1.getSequence(), token2.getSequence());
    }
}

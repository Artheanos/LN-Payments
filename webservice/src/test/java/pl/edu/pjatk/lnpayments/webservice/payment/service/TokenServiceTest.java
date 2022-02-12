package pl.edu.pjatk.lnpayments.webservice.payment.service;

import org.junit.jupiter.api.Test;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private final TokenService tokenService = new TokenService();

    @Test
    void shouldGenerateCorrectNumberOfUniqueTokens() {
        Payment payment = new Payment("asd", 5, 5, 5, PaymentStatus.PENDING);

        Collection<Token> tokens = tokenService.generateTokens(payment);

        assertThat(tokens).hasSize(5);
        assertThat(tokens).extracting(Token::getValue).allMatch(value -> value.length() == 64);
    }
}

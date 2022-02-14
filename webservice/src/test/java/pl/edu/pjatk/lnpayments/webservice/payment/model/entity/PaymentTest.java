package pl.edu.pjatk.lnpayments.webservice.payment.model.entity;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    void shouldAddTokensToSet() {
        Token token1 = new Token("token1");
        Token token2 = new Token("token2");
        Set<Token> tokens = Set.of(token1, token2);
        Payment payment = new Payment();

        payment.assignTokens(tokens);

        assertThat(payment.getTokens()).hasSize(2);
        assertThat(payment.getTokens()).contains(token1, token2);
    }

}

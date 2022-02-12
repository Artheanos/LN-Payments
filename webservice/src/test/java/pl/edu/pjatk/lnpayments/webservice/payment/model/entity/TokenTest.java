package pl.edu.pjatk.lnpayments.webservice.payment.model.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

    @Test
    void shouldHashCodeWork() {
        Token token1 = new Token("asd");
        Token token2 = new Token("asd");
        Token token3 = new Token("dsa");

        assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
        assertThat(token1.hashCode()).isNotEqualTo(token3.hashCode());
        assertThat(token2.hashCode()).isNotEqualTo(token3.hashCode());
    }

    @Test
    void shouldEqualsWork() {
        Token token1 = new Token("asd");
        Token token2 = new Token("asd");
        Token token3 = new Token("dsa");

        assertThat(token1).isEqualTo(token1);
        assertThat(token1).isNotEqualTo(null);
        assertThat(token1).isNotEqualTo(new Object());
        assertThat(token1).isEqualTo(token2);
        assertThat(token1).isNotEqualTo(token3);
        assertThat(token2).isNotEqualTo(token3);
    }
}

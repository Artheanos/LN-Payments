package pl.edu.pjatk.lnpayments.webservice.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final String testSecret = "2137";
    private final int testExpirationTime = 1645124481;
    private final String testEmail = "test@test.pl";
    private final JwtService jwtService = new JwtService(testExpirationTime, testSecret);
    private String testJwt;

    @BeforeEach
    void setUp() {
        long testCurrentTime = System.currentTimeMillis();
        this.testJwt = Jwts.builder()
                .setSubject(testEmail)
                .setIssuedAt(new Date(testCurrentTime))
                .setExpiration(new Date(testCurrentTime + testExpirationTime))
                .signWith(SignatureAlgorithm.HS512, testSecret)
                .compact();
    }

    @Test
    void shouldGenerateTokenFromEmail() {
        String token = jwtService.generateToken(testEmail);

        Claims body = Jwts.parser().setSigningKey(testSecret).parseClaimsJws(token).getBody();

        assertThat(body.getSubject()).isEqualTo(testEmail);
        assertThat(body.getExpiration()).isAfter(body.getIssuedAt());
    }

    @Test
    void shouldReturnTrueForValidToken() {
        boolean result = jwtService.isTokenValid(testJwt);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForRandomString() {
        boolean result = jwtService.isTokenValid("fesfsef");

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseForEmptyString() {
        boolean result = jwtService.isTokenValid("");

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseForRandomToken() {
        boolean result = jwtService.isTokenValid("""
                eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOi
                IxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0I
                joxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
                """);

        assertThat(result).isFalse();
    }

    @Test
    void shouldRetrieveEmailFromToken() {
        String email = jwtService.retrieveEmail(testJwt);

        assertThat(email).isEqualTo(testEmail);
    }

    @Test
    void shouldGenerateRenewedValidToken() {
        String newToken = jwtService.refreshToken(testJwt);
        String retrievedEmail = jwtService.retrieveEmail(newToken);

        assertThat(jwtService.isTokenValid(newToken)).isTrue();
        assertThat(retrievedEmail).isEqualTo(testEmail);
    }

}

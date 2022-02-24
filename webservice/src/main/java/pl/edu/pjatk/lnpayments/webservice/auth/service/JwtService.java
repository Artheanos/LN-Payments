package pl.edu.pjatk.lnpayments.webservice.auth.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class JwtService {

    private final int expirationInMs;
    private final String secret;

    public JwtService(
            @Value("${lnp.auth.expirationInMs}") int expirationInMs,
            @Value("${lnp.auth.jwtSecret}") String secret) {
        this.expirationInMs = expirationInMs;
        this.secret = secret;
    }

    public String generateToken(String email) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + expirationInMs))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String refreshToken(String token) {
        String email = retrieveEmail(token);
        return generateToken(email);
    }

    public String retrieveEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public Optional<String> headerToToken(String header) {
        if (header == null) return Optional.empty();

        String[] split = header.split(" ");
        if (split.length != 2) return Optional.empty();

        return Optional.of(split[1]);
    }
}

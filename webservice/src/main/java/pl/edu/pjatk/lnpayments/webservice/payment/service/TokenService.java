package pl.edu.pjatk.lnpayments.webservice.payment.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;

import java.util.Collection;
import java.util.HashSet;

@Service
public class TokenService {

    public Collection<Token> generateTokens(Payment payment) {
        Collection<Token> tokens = new HashSet<>();
        for (int seedIndex = 0; seedIndex < payment.getNumberOfTokens(); seedIndex++) {
            tokens.add(new Token(calculateTokenHash(payment, seedIndex)));
        }
        return tokens;
    }

    private String calculateTokenHash(Payment payment, int seed) {
        String builder = String.valueOf(payment.getNumberOfTokens()) +
                payment.getPrice() +
                payment.getId() +
                payment.getDate() +
                payment.getExpiry() +
                seed;
        return DigestUtils.sha256Hex(builder);
    }

}

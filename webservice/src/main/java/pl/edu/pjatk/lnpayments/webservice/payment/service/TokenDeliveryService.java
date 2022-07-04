package pl.edu.pjatk.lnpayments.webservice.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenDeliveryService {
    private final PropertyService propertyService;
    private Collection<Token> tokens;

    @Autowired
    TokenDeliveryService(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Async
    public void send(Collection<Token> tokens) {
        this.tokens = tokens;
        if (sendTokensAndReturnTrueIfResponseIsValid()) {
            markTokensAsDelivered();
        } else {
            logWarning();
        }
    }

    private boolean sendTokensAndReturnTrueIfResponseIsValid() {
        try {
            ResponseEntity<String> response = sendRequest();
            return response.getStatusCodeValue() == 200;
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    private void markTokensAsDelivered() {
        this.tokens.forEach(token -> token.setDelivered(true));
    }

    private void logWarning() {
        log.warn("Unsaved tokens: {}", tokens.stream().map(Token::getSequence).toArray());
    }

    private ResponseEntity<String> sendRequest() {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(buildBody(), buildHeaders());

        return new RestTemplate().postForEntity(propertyService.getTokenDeliveryUrl(), request, String.class);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Map<String, Object> buildBody() {
        Map<String, Object> map = new HashMap<>();
        map.put("tokens", tokens);
        return map;
    }
}

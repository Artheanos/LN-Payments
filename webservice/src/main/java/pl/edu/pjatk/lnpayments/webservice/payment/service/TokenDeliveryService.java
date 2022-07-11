package pl.edu.pjatk.lnpayments.webservice.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenDeliveryService {
    private final PropertyService propertyService;
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    @Autowired
    TokenDeliveryService(RestTemplate restBuilder, PropertyService propertyService) {
        this.restTemplate = restBuilder;
        this.propertyService = propertyService;
        this.headers = buildHeaders();
    }

    @Async
    public void send(Collection<Token> tokens) {
        if (tryDeliveringTokens(tokens)) {
            markTokensAsDelivered(tokens);
        } else {
            logWarning(tokens);
        }
    }

    private boolean tryDeliveringTokens(Collection<Token> tokens) {
        if (StringUtils.isEmpty(propertyService.getTokenDeliveryUrl())) {
            log.warn("Destination URL not set");
            return false;
        }

        try {
            ResponseEntity<String> response = sendRequest(tokens);
            return response.getStatusCodeValue() == 200;
        } catch (ResourceAccessException | HttpClientErrorException e) {
            return false;
        }
    }

    private void markTokensAsDelivered(Collection<Token> tokens) {
        tokens.forEach(token -> token.setDelivered(true));
    }

    private void logWarning(Collection<Token> tokens) {
        log.warn("Tokens were not delivered: {}", tokens.stream().map(Token::getSequence).toArray());
    }

    private ResponseEntity<String> sendRequest(Collection<Token> tokens) throws ResourceAccessException {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(buildBody(tokens), headers);

        return restTemplate.postForEntity(propertyService.getTokenDeliveryUrl(), request, String.class);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    private Map<String, Object> buildBody(Collection<Token> tokens) {
        Map<String, Object> map = new HashMap<>();
        map.put("tokens", tokens);

        return map;
    }
}

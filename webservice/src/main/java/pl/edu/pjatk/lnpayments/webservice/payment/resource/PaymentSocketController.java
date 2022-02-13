package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.converter.TokenConverter;

import java.util.Collection;

@Slf4j
@Controller
public class PaymentSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final TokenConverter tokenConverter;

    @Autowired
    public PaymentSocketController(SimpMessagingTemplate messagingTemplate, TokenConverter tokenConverter) {
        this.messagingTemplate = messagingTemplate;
        this.tokenConverter = tokenConverter;
    }

    public void sendTokens(String channelId, Collection<Token> tokens) {
        this.messagingTemplate.convertAndSend("/topic/" + channelId, tokenConverter.convertAllToDto(tokens));
    }
}

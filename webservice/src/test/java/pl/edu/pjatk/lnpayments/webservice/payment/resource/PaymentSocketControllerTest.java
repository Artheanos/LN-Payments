package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.converter.TokenConverter;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.TokenResponse;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentSocketControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private TokenConverter tokenConverter;

    @InjectMocks
    private PaymentSocketController paymentSocketController;

    @Test
    void shouldSendMessage() {
        Token token1 = new Token("token1");
        Token token2 = new Token("token2");
        Set<Token> tokens = Set.of(token1, token2);
        String channelId = "id";
        when(tokenConverter.convertAllToDto(tokens)).thenReturn(new TokenResponse(Collections.emptySet()));

        paymentSocketController.sendTokens(channelId, tokens);

        verify(messagingTemplate).convertAndSend(eq("/topic/" + channelId), any(TokenResponse.class));
        verify(tokenConverter).convertAllToDto(tokens);
    }

}
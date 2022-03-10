package pl.edu.pjatk.lnpayments.webservice.auth.interceptor;

import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.edu.pjatk.lnpayments.webservice.auth.model.UserDetailsImpl;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthChannelInterceptorTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthChannelInterceptor authChannelInterceptor;

    @Test
    void shouldAuthenticateUserWithValidToken() {
        String token = "token";
        String email = "test@test.pl";

        StompHeaderAccessor stompAccessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        stompAccessor.setNativeHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        stompAccessor.setLeaveMutable(true);
        MessageHeaders headers = stompAccessor.getMessageHeaders();
        Message<String> message = MessageBuilder.createMessage("payload", headers);

        UserDetailsImpl userDetails = new UserDetailsImpl(email, "", Role.ROLE_USER);
        when(jwtService.headerToToken("Bearer " + token)).thenReturn(Optional.of(token));
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.retrieveEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        authChannelInterceptor.preSend(message, null);

        verify(userDetailsService).loadUserByUsername(email);
        verify(jwtService).retrieveEmail(token);
        verify(jwtService).isTokenValid(token);
    }

    @Test
    void shouldNotAuthenticateOtherRequests() {
        String token = "token";
        String email = "test@test.pl";

        StompHeaderAccessor stompAccessor = StompHeaderAccessor.create(StompCommand.STOMP);
        stompAccessor.setNativeHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        stompAccessor.setLeaveMutable(true);
        MessageHeaders headers = stompAccessor.getMessageHeaders();
        Message<String> message = MessageBuilder.createMessage("payload", headers);

        authChannelInterceptor.preSend(message, null);

        verify(jwtService, never()).headerToToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(email);
    }

    @Test
    void shouldNotAuthenticateWhenInvalidToken() {
        String token = "token";
        StompHeaderAccessor stompAccessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        stompAccessor.setNativeHeader(HttpHeaders.AUTHORIZATION, "Bearer ddd");
        stompAccessor.setLeaveMutable(true);
        MessageHeaders headers = stompAccessor.getMessageHeaders();
        Message<String> message = MessageBuilder.createMessage("payload", headers);
        when(jwtService.isTokenValid(token)).thenReturn(false);
        when(jwtService.headerToToken(anyString())).thenReturn(Optional.of(token));

        authChannelInterceptor.preSend(message, null);

        verify(jwtService).isTokenValid(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}

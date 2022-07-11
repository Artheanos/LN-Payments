package pl.edu.pjatk.lnpayments.webservice.payment.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenDeliveryServiceTest {
    private ListAppender<ILoggingEvent> logAppender;

    @Mock
    private PropertyService propertyService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TokenDeliveryService tokenDeliveryService;

    public static Collection<Token> tokens;

    @BeforeEach
    void setUpEach() {
        Logger logger = (Logger) LoggerFactory.getLogger(TokenDeliveryService.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);

        tokens = Collections.singleton(new Token("aaa"));
    }

    @Nested
    class WhenDestinationUrlIsEmpty {
        @BeforeEach
        void setUp() {
            when(propertyService.getTokenDeliveryUrl()).thenReturn("");
        }

        @Test
        void shouldLogWarning() {
            tokenDeliveryService.send(tokens);
            assertEquals("Destination URL not set", logAppender.list.get(0).getMessage());
        }
    }

    @Nested
    class WhenConfigurationIsValid {
        @BeforeEach
        void setUp() {
            when(propertyService.getTokenDeliveryUrl()).thenReturn("http://localhost:8080");
        }

        @Test
        @SneakyThrows
        void shouldSendProperRequest() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> map = new HashMap<>();
            map.put("tokens", tokens);

            when(restTemplate.postForEntity(any(String.class), any(), any())).thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));
            tokenDeliveryService.send(tokens);
            verify(restTemplate).postForEntity(eq("http://localhost:8080"), eq(new HttpEntity<>(map, headers)), eq(String.class));
        }

        @Nested
        class WhenResponseHasOkStatus {
            @Test
            @SneakyThrows
            void shouldUpdateTokens() {
                when(restTemplate.postForEntity(any(String.class), any(), any())).thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));
                assertTrue(tokens.stream().noneMatch(Token::isDelivered));
                tokenDeliveryService.send(tokens);
                assertTrue(tokens.stream().allMatch(Token::isDelivered));
            }
        }

        @Nested
        class WhenResponseDoesNotHaveOkStatus {
            @Test
            @SneakyThrows
            void shouldNotUpdateTokens() {
                when(restTemplate.postForEntity(any(String.class), any(), any())).thenReturn(new ResponseEntity<>("not ok", HttpStatus.BAD_REQUEST));
                tokenDeliveryService.send(tokens);

                assertTrue(tokens.stream().noneMatch(Token::isDelivered));

                assertEquals("Tokens were not delivered: {}", logAppender.list.get(0).getMessage());
                assertEquals("aaa", logAppender.list.get(0).getArgumentArray()[0]);
            }
        }
    }
}

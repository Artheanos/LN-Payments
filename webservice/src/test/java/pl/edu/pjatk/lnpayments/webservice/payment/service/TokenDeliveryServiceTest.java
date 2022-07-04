package pl.edu.pjatk.lnpayments.webservice.payment.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenDeliveryServiceTest {
    private ListAppender<ILoggingEvent> logAppender;

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private TokenDeliveryService tokenDeliveryService;

    public static MockWebServer mockWebServer;
    public static Collection<Token> tokens;

    @BeforeEach
    void setUpEach() {
        Logger logger = (Logger) LoggerFactory.getLogger(InvoiceService.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);

        when(propertyService.getTokenDeliveryUrl()).thenReturn("http://localhost:" + mockWebServer.getPort());
        tokens = Collections.singleton(new Token("aaa"));
    }

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @SneakyThrows
    void shouldSendProperRequest() {
        mockWebServer.enqueue(new MockResponse());
        tokenDeliveryService.send(tokens);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("{\"tokens\":[{\"id\":null,\"sequence\":\"aaa\",\"delivered\":false}]}", recordedRequest.getBody().readUtf8());
    }

    @Nested
    class WhenResponseHasOkStatus {
        @Test
        @SneakyThrows
        void shouldUpdateTokens() {
            mockWebServer.enqueue(new MockResponse());
            assertTrue(tokens.stream().noneMatch(Token::isDelivered));

            tokenDeliveryService.send(tokens);
            mockWebServer.takeRequest();
            assertTrue(tokens.stream().allMatch(Token::isDelivered));
        }
    }

    @Nested
    class WhenResponseDoesNotHaveOkStatus {
        @Test
        @SneakyThrows
        void shouldNotUpdateTokens() {
            MockResponse response = new MockResponse();
            response.setResponseCode(401);

            mockWebServer.enqueue(response);
            tokenDeliveryService.send(tokens);
            mockWebServer.takeRequest();

            assertTrue(tokens.stream().noneMatch(Token::isDelivered));
        }
    }
}

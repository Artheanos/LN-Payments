package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.TemporaryUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;
import pl.edu.pjatk.lnpayments.webservice.helper.TestSocketFrameHandler;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.observer.InvoiceObserver;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.PaymentRepository;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.TokenResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(classes = {IntegrationTestConfiguration.class}, webEnvironment = RANDOM_PORT)
class PaymentSocketControllerIntegrationTest extends BaseIntegrationTest {

    protected static WireMockServer mockExternalServer = new WireMockServer(9000);

    @LocalServerPort
    private int port;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private InvoiceObserver invoiceObserver;

    private WebSocketStompClient webSocketStompClient;

    private TestSocketFrameHandler<TokenResponse> handler;

    @BeforeEach
    void setUp() {
        webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        invoiceObserver = new InvoiceObserver(paymentFacade);
        handler = TestSocketFrameHandler.of(TokenResponse.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        paymentRepository.deleteAll();
        webSocketStompClient.stop();
    }

    @BeforeAll
    static void setUpBeforeAll() {
        mockExternalServer.start();
        configureFor("localhost", 9000);
        stubFor(post(urlEqualTo("/tokens")).willReturn(aResponse().withStatus(200)));
    }

    @AfterAll
    static void tearDownAfterAll() {
        mockExternalServer.stop();
    }

    @Test
    void shouldSendStompMessageWhenInvoiceIsSettled() throws ExecutionException, InterruptedException, TimeoutException {
        String paymentRequest = "dududu";
        User user = new TemporaryUser("test@test.pl");
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        Invoice invoice = new Invoice();
        invoice.setState(Invoice.InvoiceState.SETTLED);
        invoice.setPaymentRequest(paymentRequest);
        Payment payment = new Payment(paymentRequest, 1, 1, 60, PaymentStatus.PENDING, null);
        paymentRepository.save(payment);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        StompSession stompSession = webSocketStompClient.connect(
                String.format("ws://localhost:%d/api/payment", port),
                new WebSocketHttpHeaders(httpHeaders),
                new StompSessionHandlerAdapter() {
                }).get(1, SECONDS);
        stompSession.subscribe("/topic/34079ad7", handler);
        SECONDS.sleep(1);

        invoiceObserver.onNext(invoice);

        TokenResponse tokenResponse = handler.getResult();
        assertThat(tokenResponse.getTokens()).hasSize(1);

        String expectedRequestBody = String.format("{\"tokens\":[{\"id\":1,\"sequence\":\"%s\",\"delivered\":false}]}", tokenResponse.getTokens().stream().findFirst().get());
        SECONDS.sleep(1);
        verify(postRequestedFor(urlEqualTo("/tokens")).withRequestBody(containing(expectedRequestBody)));
    }

    @Test
    void shouldReturn401WhenNoUser() {
        String email = "test@test.pl";
        String token = jwtService.generateToken(email);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        assertThatExceptionOfType(ExecutionException.class).isThrownBy(() ->
                webSocketStompClient.connect(
                        String.format("ws://localhost:%d/api/payment", port),
                        new WebSocketHttpHeaders(httpHeaders),
                        new StompSessionHandlerAdapter() {
                        }).get(1, SECONDS)
        );
    }

}


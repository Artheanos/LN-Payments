package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.DisabledSecurityConfig;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.observer.InvoiceObserver;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.TokenResponse;
import pl.edu.pjatk.lnpayments.webservice.payment.service.PaymentDataService;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(classes = {IntegrationTestConfiguration.class, DisabledSecurityConfig.class}, webEnvironment = RANDOM_PORT)
class PaymentSocketControllerIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PaymentDataService paymentDataService;

    private InvoiceObserver invoiceObserver;

    private WebSocketStompClient webSocketStompClient;

    private final CompletableFuture<TokenResponse> completableFuture = new CompletableFuture<>();

    @BeforeEach
    void setUp() {
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        invoiceObserver = new InvoiceObserver(paymentFacade);
    }

    @Test
    void shouldSendStompMessageWhenInvoiceIsSettled() throws ExecutionException, InterruptedException, TimeoutException {
        String paymentRequest = "dududu";
        Invoice invoice = new Invoice();
        invoice.setState(Invoice.InvoiceState.SETTLED);
        invoice.setPaymentRequest(paymentRequest);
        Payment payment = new Payment(paymentRequest, 1, 1, 60, PaymentStatus.PENDING, null);
        paymentDataService.savePayment(payment);
        StompSession stompSession = webSocketStompClient.connect(String.format("ws://localhost:%d/api/payment", port), new StompSessionHandlerAdapter() {}).get(1, SECONDS);
        stompSession.subscribe("/topic/34079ad7", new TokenResponseFrameHandler());

        invoiceObserver.onNext(invoice);

        TokenResponse tokenResponse = completableFuture.get(5, SECONDS);
        assertThat(tokenResponse.getTokens()).hasSize(1);
    }

    private class TokenResponseFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return TokenResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((TokenResponse) o);
        }
    }
}


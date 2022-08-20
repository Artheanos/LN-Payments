package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.helper.TestSocketFrameHandler;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.NotificationResponse;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ActiveProfiles("test")
@SpringBootTest(classes = {IntegrationTestConfiguration.class}, webEnvironment = RANDOM_PORT)
class NotificationSocketControllerIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private NotificationSocketController socketController;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private WebSocketStompClient webSocketStompClient;

    private TestSocketFrameHandler<NotificationResponse> handler;

    @BeforeEach
    void setUp() {
        webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        handler = TestSocketFrameHandler.of(NotificationResponse.class);
    }

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();
        transactionRepository.deleteAll();
        userRepository.deleteAll();
        webSocketStompClient.stop();
    }

    @Test
    void shouldSendNotification() throws ExecutionException, InterruptedException, TimeoutException {
        AdminUser user = createAdminUser("test@test.pl");
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        Transaction transaction = new Transaction("aaa", "bbb", "ccc", 1L, 1L, 1);
        transactionRepository.save(transaction);
        Notification notification = new Notification(user, transaction, "message", NotificationType.TRANSACTION);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        StompSession stompSession = webSocketStompClient.connect(
                String.format("ws://localhost:%d/api/notification", port),
                new WebSocketHttpHeaders(httpHeaders),
                new StompSessionHandlerAdapter() {
                }).get(1, SECONDS);
        stompSession.subscribe("/topic/" + user.notificationsChannelId(), handler);
        SECONDS.sleep(1);

        socketController.sendAllNotifications(List.of(notification));

        NotificationResponse response = handler.getResult();
        assertThat(response.getMessage()).isEqualTo("message");
    }
}

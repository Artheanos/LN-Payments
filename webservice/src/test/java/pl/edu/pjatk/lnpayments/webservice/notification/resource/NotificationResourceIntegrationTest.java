package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class NotificationResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockBean
    private Principal principal;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @BeforeEach
    void setUp() {
        when(principal.getName()).thenReturn("user1@test.pl");
    }

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();
        adminUserRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    void shouldReturnNotificationsByUser() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTargetAddress("123");
        transaction.setInputValue(1L);
        transaction.setFee(1L);
        Transaction transaction2 = new Transaction();
        transaction2.setTargetAddress("123");
        transaction2.setInputValue(1L);
        transaction2.setFee(1L);
        transactionRepository.save(transaction);
        transactionRepository.save(transaction2);
        AdminUser user1 = createAdminUser("user1@test.pl");
        AdminUser user2 = createAdminUser("user2@test.pl");
        adminUserRepository.saveAll(List.of(user1, user2));
        List<Notification> notifications = List.of(
                new Notification(user1, transaction, "message1", NotificationType.TRANSACTION),
                new Notification(user1, transaction2, "message2", NotificationType.TRANSACTION),
                new Notification(user2, transaction, "message3", NotificationType.TRANSACTION)
        );
        notificationRepository.saveAll(notifications);
        String jsonContent = getJsonResponse("integration/payment/response/notifications-GET.json");

        mockMvc.perform(get("/notifications").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturnOkAndConfirmationDetails() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setRawTransaction("rawtx");
        transaction.setTargetAddress("123");
        transaction.setInputValue(1L);
        transaction.setFee(1L);
        transactionRepository.save(transaction);
        AdminUser user = createAdminUser("user1@test.pl");
        adminUserRepository.save(user);
        Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        notificationRepository.save(notification);
        String jsonContent = getJsonResponse("integration/payment/response/confirmation-details-GET.json");

        mockMvc.perform(get("/notifications/d6b5915c46/transaction"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturn404WhenNoNotification() throws Exception {
        mockMvc.perform(get("/notifications/d6b5915c46/transaction"))
                .andExpect(status().isNotFound());
    }
}

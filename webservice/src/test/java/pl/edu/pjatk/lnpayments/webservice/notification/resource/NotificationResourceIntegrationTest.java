package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.dto.ConfirmationDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        mockMvc.perform(get("/notifications/" + notification.getIdentifier() + "/transaction"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturn404WhenNoNotification() throws Exception {
        mockMvc.perform(get("/notifications/d6b5915c46/transaction"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkAndDenyNotification() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setRequiredApprovals(1);
        transactionRepository.save(transaction);
        AdminUser user = createAdminUser("user1@test.pl");
        adminUserRepository.save(user);
        Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        notificationRepository.save(notification);

        mockMvc.perform(post("/notifications/" + notification.getIdentifier() + "/deny"))
                .andExpect(status().isOk());

        Optional<Notification> saved = notificationRepository.findByIdentifier(notification.getIdentifier());
        assertThat(saved.orElseThrow().getStatus()).isEqualTo(NotificationStatus.DENIED);
    }

    @Test
    void shouldReturn404WhenNotificationForDenialNotFound() throws Exception {
        mockMvc.perform(post("/notifications/d6b5915c46/deny"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenNotificationForDenialIsAlreadyFinalized() throws Exception {
        Transaction transaction = new Transaction();
        transactionRepository.save(transaction);
        AdminUser user = createAdminUser("user1@test.pl");
        adminUserRepository.save(user);
        Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        notification.setStatus(NotificationStatus.DENIED);
        notificationRepository.save(notification);

        mockMvc.perform(post("/notifications/" + notification.getIdentifier() + "/deny"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOkAndConfirmNotification() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setRequiredApprovals(2);
        transactionRepository.save(transaction);
        AdminUser user = createAdminUser("user1@test.pl");
        user.setPublicKey("0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb");
        adminUserRepository.save(user);
        Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        notificationRepository.save(notification);
        ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L);

        mockMvc.perform(post("/notifications/" + notification.getIdentifier() + "/confirm")
                        .content(new ObjectMapper().writeValueAsBytes(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Notification saved = notificationRepository.findByIdentifier(notification.getIdentifier()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.CONFIRMED);
        assertThat(saved.getTransaction().getRawTransaction()).isEqualTo(details.getRawTransaction());
        assertThat(saved.getTransaction().getVersion()).isEqualTo(details.getVersion() + 1);
    }

    @Test
    void shouldReturn404WhenNotificationForConfirmationNotFound() throws Exception {
        ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L);
        mockMvc.perform(post("/notifications/d6b5915c46/confirm")
                    .content(new ObjectMapper().writeValueAsBytes(details))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenNotificationForConfirmationAlreadyFinalized() throws Exception {
        Transaction transaction = new Transaction();
        transactionRepository.save(transaction);
        AdminUser user = createAdminUser("user1@test.pl");
        adminUserRepository.save(user);
        Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        notification.setStatus(NotificationStatus.DENIED);
        notificationRepository.save(notification);
        ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L);

        mockMvc.perform(post("/notifications/" + notification.getIdentifier() + "/confirm")
                        .content(new ObjectMapper().writeValueAsBytes(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn412ForConfirmationWhenVersionIsInvalid() throws Exception {
        Transaction transaction = new Transaction();
        transactionRepository.save(transaction);
        AdminUser user = createAdminUser("user1@test.pl");
        user.setPublicKey("0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb");
        adminUserRepository.save(user);
        Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        notificationRepository.save(notification);
        ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 1L);

        mockMvc.perform(post("/notifications/" + notification.getIdentifier() + "/confirm")
                        .content(new ObjectMapper().writeValueAsBytes(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    void shouldReturn400ForConfirmationWhenSignatureIsInvalid() throws Exception {
        Transaction transaction = new Transaction();
        transactionRepository.save(transaction);
        AdminUser user = createAdminUser("user1@test.pl");
        user.setPublicKey("0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb");
        adminUserRepository.save(user);
        Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
        notificationRepository.save(notification);
        ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c26010047522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L);

        mockMvc.perform(post("/notifications/" + notification.getIdentifier() + "/confirm")
                        .content(new ObjectMapper().writeValueAsBytes(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenVersionIsNegative() throws Exception {
        ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c26010047522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", -2L);

        mockMvc.perform(post("/notifications/aaa/confirm")
                        .content(new ObjectMapper().writeValueAsBytes(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenRawTxIsBlank() throws Exception {
        ConfirmationDetails details = new ConfirmationDetails("", -2L);

        mockMvc.perform(post("/notifications/aaa/confirm")
                        .content(new ObjectMapper().writeValueAsBytes(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenRawTxIsNull() throws Exception {
        ConfirmationDetails details = new ConfirmationDetails(null, 1L);

        mockMvc.perform(post("/notifications/aaa/confirm")
                        .content(new ObjectMapper().writeValueAsBytes(details))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

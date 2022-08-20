package pl.edu.pjatk.lnpayments.webservice.notification.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.TransactionBroadcast;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

import java.security.Principal;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletAppKit walletAppKit;

    @BeforeEach
    void setUp() {
        when(principal.getName()).thenReturn("user1@test.pl");
    }

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();
        adminUserRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Nested
    class GetNotifications {
        Notification notification1;

        @BeforeEach
        void setUp() {
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

            notification1 = new Notification(user1, transaction, "message1", NotificationType.TRANSACTION);
            List<Notification> notifications = List.of(
                    notification1,
                    new Notification(user1, transaction2, "message2", NotificationType.TRANSACTION),
                    new Notification(user2, transaction, "message3", NotificationType.TRANSACTION)
            );
            notificationRepository.saveAll(notifications);
        }

        @Test
        void shouldReturnNotificationById() throws Exception {
            String jsonContent = getJsonResponse("integration/payment/response/notification-GET.json");

            mockMvc.perform(get("/api/notifications/" + notification1.getIdentifier()).principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(content().json(jsonContent));
        }

        @Test
        void shouldReturn404WhenIdCannotBeFound() throws Exception {
            mockMvc.perform(get("/api/notifications/1").principal(principal))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnNotificationsByUser() throws Exception {
            String jsonContent = getJsonResponse("integration/payment/response/notifications-GET.json");

            mockMvc.perform(get("/api/notifications").principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(content().json(jsonContent));
        }
    }

    @Nested
    class GetSignatureRequiredData {
        @Test
        void shouldReturnOkAndConfirmationDetails() throws Exception {
            Wallet wallet = new Wallet();
            wallet.setStatus(WalletStatus.ON_DUTY);
            wallet.setRedeemScript("redeem");
            walletRepository.save(wallet);
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

            mockMvc.perform(get("/api/notifications/" + notification.getIdentifier() + "/transaction"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(jsonContent));
        }

        @Test
        void shouldReturn404WhenNoNotification() throws Exception {
            mockMvc.perform(get("/api/notifications/d6b5915c46/transaction"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnOkAndDenyNotificationAndTransaction() throws Exception {
            Transaction transaction = new Transaction();
            transaction.setRequiredApprovals(1);
            transactionRepository.save(transaction);
            AdminUser user = createAdminUser("user1@test.pl");
            adminUserRepository.save(user);
            Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
            notificationRepository.save(notification);

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/deny"))
                    .andExpect(status().isOk());

            Notification saved = notificationRepository.findByIdentifier(notification.getIdentifier()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(NotificationStatus.DENIED);
            assertThat(saved.getTransaction().getStatus()).isEqualTo(TransactionStatus.DENIED);
        }

        @Test
        void shouldReturnOkAndDenyNotificationAndNotDenyTransaction() throws Exception {
            Transaction transaction = new Transaction();
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setRequiredApprovals(2);
            transactionRepository.save(transaction);
            AdminUser user = createAdminUser("user1@test.pl");
            adminUserRepository.save(user);
            Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
            notificationRepository.save(notification);

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/deny"))
                    .andExpect(status().isOk());

            Notification saved = notificationRepository.findByIdentifier(notification.getIdentifier()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(NotificationStatus.DENIED);
            assertThat(saved.getTransaction().getStatus()).isEqualTo(TransactionStatus.PENDING);
        }
    }

    @Nested
    class DenyNotification {
        @Test
        void shouldReturn404WhenNotificationForDenialNotFound() throws Exception {
            mockMvc.perform(post("/api/notifications/d6b5915c46/deny"))
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

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/deny"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldRemoveWalletInCreationWhenTransactionIsDenied() throws Exception {
            Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList(), 1);
            wallet.setStatus(WalletStatus.IN_CREATION);
            walletRepository.save(wallet);
            Transaction transaction = new Transaction();
            transaction.setRequiredApprovals(1);
            transactionRepository.save(transaction);
            AdminUser user = createAdminUser("user1@test.pl");
            adminUserRepository.save(user);
            Notification notification = new Notification(user, transaction, "message1", NotificationType.WALLET_RECREATION);
            notificationRepository.save(notification);

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/deny"))
                    .andExpect(status().isOk());

            Notification saved = notificationRepository.findByIdentifier(notification.getIdentifier()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(NotificationStatus.DENIED);
            assertThat(saved.getTransaction().getStatus()).isEqualTo(TransactionStatus.DENIED);
            assertThat(walletRepository.existsByStatus(WalletStatus.IN_CREATION)).isFalse();
        }
    }

    @Nested
    class ConfirmNotification {
        @Test
        void shouldReturnOkAndConfirmNotificationAndApproveTransaction() throws Exception {
            AdminUser user = createAdminUser("user1@test.pl");
            user.setPublicKey("02ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a");
            AdminUser adminUser = adminUserRepository.save(user);
            Wallet wallet = new Wallet("522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae", "456", "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4", List.of(adminUser), 1);
            wallet.setStatus(WalletStatus.ON_DUTY);
            walletRepository.save(wallet);
            Transaction transaction = new Transaction();
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setRequiredApprovals(1);
            transactionRepository.save(transaction);
            Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
            notificationRepository.save(notification);
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L, null);
            RegTestParams params = RegTestParams.get();
            org.bitcoinj.core.Transaction transaction1 = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex(details.getRawTransaction()));
            org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
            PeerGroup peerGroup = Mockito.mock(PeerGroup.class);
            TransactionBroadcast transactionBroadcast = Mockito.mock(TransactionBroadcast.class);
            ListenableFutureTask<org.bitcoinj.core.Transaction> dumbTask = ListenableFutureTask.create(() -> transaction1);
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(dumbTask);
            when(transactionBroadcast.broadcast()).thenReturn(dumbTask);
            when(peerGroup.broadcastTransaction(transaction1)).thenReturn(transactionBroadcast);
            when(walletAppKit.peerGroup()).thenReturn(peerGroup);
            when(walletAppKit.wallet()).thenReturn(walletMock);
            when(walletAppKit.params()).thenReturn(params);
            when(walletMock.getWatchedOutputs(true)).thenReturn(transaction1.getOutputs());
            when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(100L));
            when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(0L));

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/confirm")
                            .content(new ObjectMapper().writeValueAsBytes(details))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Notification saved = notificationRepository.findByIdentifier(notification.getIdentifier()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(NotificationStatus.CONFIRMED);
            assertThat(saved.getTransaction().getRawTransaction()).isEqualTo(details.getRawTransaction());
            assertThat(saved.getTransaction().getVersion()).isEqualTo(details.getVersion() + 1);
            assertThat(saved.getTransaction().getStatus()).isEqualTo(TransactionStatus.APPROVED);
        }

        @Test
        void shouldReturnOkAndConfirmNotificationAndNotConfirmTransaction() throws Exception {
            AdminUser user = createAdminUser("user1@test.pl");
            user.setPublicKey("02ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a");
            AdminUser adminUser = adminUserRepository.save(user);
            Wallet wallet = new Wallet("522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae", "456", "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4", List.of(adminUser), 2);
            walletRepository.save(wallet);
            Transaction transaction = new Transaction();
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setRequiredApprovals(2);
            transactionRepository.save(transaction);
            Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
            notificationRepository.save(notification);
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L, null);

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/confirm")
                            .content(new ObjectMapper().writeValueAsBytes(details))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Notification saved = notificationRepository.findByIdentifier(notification.getIdentifier()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(NotificationStatus.CONFIRMED);
            assertThat(saved.getTransaction().getRawTransaction()).isEqualTo(details.getRawTransaction());
            assertThat(saved.getTransaction().getVersion()).isEqualTo(details.getVersion() + 1);
            assertThat(saved.getTransaction().getStatus()).isEqualTo(TransactionStatus.PENDING);
        }

        @Test
        void shouldReturnOkAndFailTransactionIfDataIsInvalid() throws Exception {
            AdminUser user = createAdminUser("user1@test.pl");
            user.setPublicKey("0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb");
            AdminUser adminUser = adminUserRepository.save(user);
            Wallet wallet = new Wallet("522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae", "456", "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4", List.of(adminUser), 1);
            wallet.setStatus(WalletStatus.ON_DUTY);
            walletRepository.save(wallet);
            Transaction transaction = new Transaction();
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setRequiredApprovals(1);
            transactionRepository.save(transaction);
            Notification notification = new Notification(user, transaction, "message1", NotificationType.TRANSACTION);
            notificationRepository.save(notification);
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L, null);

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/confirm")
                            .content(new ObjectMapper().writeValueAsBytes(details))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Notification saved = notificationRepository.findByIdentifier(notification.getIdentifier()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(NotificationStatus.CONFIRMED);
            assertThat(saved.getTransaction().getRawTransaction()).isEqualTo(details.getRawTransaction());
            assertThat(saved.getTransaction().getVersion()).isEqualTo(details.getVersion() + 1);
            assertThat(saved.getTransaction().getStatus()).isEqualTo(TransactionStatus.FAILED);
        }

        @Test
        void shouldReturnOkAndSwapWalletsWhenTransactionConfirmed() throws Exception {
            AdminUser user = createAdminUser("user1@test.pl");
            user.setPublicKey("02ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a");
            AdminUser adminUser = adminUserRepository.save(user);
            Wallet wallet = new Wallet("522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae", "123", "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4", List.of(adminUser), 1);
            wallet.setStatus(WalletStatus.ON_DUTY);
            walletRepository.save(wallet);
            Wallet newWallet = new Wallet("522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae", "456", "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4", List.of(adminUser), 1);
            newWallet.setStatus(WalletStatus.IN_CREATION);
            walletRepository.save(newWallet);
            Transaction transaction = new Transaction();
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setRequiredApprovals(1);
            transactionRepository.save(transaction);
            Notification notification = new Notification(user, transaction, "message1", NotificationType.WALLET_RECREATION);
            notificationRepository.save(notification);
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L, null);
            RegTestParams params = RegTestParams.get();
            org.bitcoinj.core.Transaction transaction1 = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex(details.getRawTransaction()));
            org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
            PeerGroup peerGroup = Mockito.mock(PeerGroup.class);
            TransactionBroadcast transactionBroadcast = Mockito.mock(TransactionBroadcast.class);
            ListenableFutureTask<org.bitcoinj.core.Transaction> dumbTask = ListenableFutureTask.create(() -> transaction1);
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(dumbTask);
            when(transactionBroadcast.broadcast()).thenReturn(dumbTask);
            when(peerGroup.broadcastTransaction(transaction1)).thenReturn(transactionBroadcast);
            when(walletAppKit.peerGroup()).thenReturn(peerGroup);
            when(walletAppKit.wallet()).thenReturn(walletMock);
            when(walletAppKit.params()).thenReturn(params);
            when(walletMock.getWatchedOutputs(true)).thenReturn(transaction1.getOutputs());
            when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(100L));
            when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(0L));

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/confirm")
                            .content(new ObjectMapper().writeValueAsBytes(details))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Notification saved = notificationRepository.findByIdentifier(notification.getIdentifier()).orElseThrow();
            assertThat(saved.getStatus()).isEqualTo(NotificationStatus.CONFIRMED);
            assertThat(saved.getTransaction().getRawTransaction()).isEqualTo(details.getRawTransaction());
            assertThat(saved.getTransaction().getVersion()).isEqualTo(details.getVersion() + 1);
            assertThat(saved.getTransaction().getStatus()).isEqualTo(TransactionStatus.APPROVED);
            assertThat(walletRepository.existsByStatus(WalletStatus.IN_CREATION)).isFalse();
            Wallet newSavedWallet = walletRepository.findFirstByStatus(WalletStatus.ON_DUTY).orElseThrow();
            assertThat(newSavedWallet.getScriptPubKey()).isEqualTo(newWallet.getScriptPubKey());
            assertThat(walletRepository.existsByStatus(WalletStatus.REMOVED)).isTrue();
        }

        @Test
        void shouldReturn404WhenNotificationForConfirmationNotFound() throws Exception {
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L, null);
            mockMvc.perform(post("/api/notifications/d6b5915c46/confirm")
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
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L, null);

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/confirm")
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
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 1L, null);

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/confirm")
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
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c26010047522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", 0L, null);

            mockMvc.perform(post("/api/notifications/" + notification.getIdentifier() + "/confirm")
                            .content(new ObjectMapper().writeValueAsBytes(details))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenVersionIsNegative() throws Exception {
            ConfirmationDetails details = new ConfirmationDetails("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c26010047522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", -2L, null);

            mockMvc.perform(post("/api/notifications/aaa/confirm")
                            .content(new ObjectMapper().writeValueAsBytes(details))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenRawTxIsBlank() throws Exception {
            ConfirmationDetails details = new ConfirmationDetails("", -2L, null);

            mockMvc.perform(post("/api/notifications/aaa/confirm")
                            .content(new ObjectMapper().writeValueAsBytes(details))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenRawTxIsNull() throws Exception {
            ConfirmationDetails details = new ConfirmationDetails(null, 1L, null);

            mockMvc.perform(post("/api/notifications/aaa/confirm")
                            .content(new ObjectMapper().writeValueAsBytes(details))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}

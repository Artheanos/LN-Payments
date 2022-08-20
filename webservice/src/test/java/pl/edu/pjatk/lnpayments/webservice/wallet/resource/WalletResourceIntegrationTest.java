package pl.edu.pjatk.lnpayments.webservice.wallet.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.PaymentRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.CreateWalletRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
@TestPropertySource(properties = "app.scheduling.enable=false")
class WalletResourceIntegrationTest extends BaseIntegrationTest {

    private final static String EMAIL = "test@test.pl";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private SynchronousLndAPI lndAPI;

    @Autowired
    private WalletAppKit walletAppKit;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();
        adminUserRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    void shouldReturnCreatedWhenNoIssues() throws Exception {
        String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        admin1.setPublicKey(publicKey);
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        admin2.setPublicKey(publicKey);
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        CreateWalletRequest request = new CreateWalletRequest(1, adminEmails);
        adminUserRepository.save(admin1);
        adminUserRepository.save(admin2);

        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<Wallet> wallets = walletRepository.findAll();
        assertThat(wallets.size()).isEqualTo(1);
        assertThat(wallets.get(0).getAddress()).isEqualTo("2NGEpb541CnfpmA3LEWoehMNCnG94ybTR3F");
        assertThat(wallets.get(0).getRedeemScript()).isEqualTo("51210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae");
        assertThat(wallets.get(0).getScriptPubKey()).isEqualTo("a914fc375c082884b9d7575ac04102d11218406434d287");
        assertThat(wallets.get(0).getUsers().size()).isEqualTo(2);
        assertThat(wallets.get(0).getMinSignatures()).isEqualTo(1);
    }


    @Test
    void shouldRecreateWalletWhenWalletAlreadyExists() throws Exception {
        String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        admin1.setPublicKey(publicKey);
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        admin2.setPublicKey(publicKey);
        Wallet oldWallet = new Wallet("123", "456", "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4", List.of(admin1, admin2), 2);
        oldWallet.setStatus(WalletStatus.ON_DUTY);
        walletRepository.save(oldWallet);
        admin1.setWallet(oldWallet);
        admin2.setWallet(oldWallet);
        adminUserRepository.save(admin1);
        adminUserRepository.save(admin2);
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        CreateWalletRequest request = new CreateWalletRequest(1, adminEmails);
        NetworkParameters params = RegTestParams.get();
        org.bitcoinj.core.Transaction transaction1 = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000"));
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletAppKit.wallet()).thenReturn(walletMock);
        when(walletAppKit.params()).thenReturn(params);
        when(walletMock.getWatchedOutputs(true)).thenReturn(transaction1.getOutputs());
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(1000L));
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(0L));

        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Wallet wallet = walletRepository.findFirstByStatus(WalletStatus.IN_CREATION).orElseThrow();
        assertThat(walletRepository.existsByStatus(WalletStatus.ON_DUTY)).isTrue();
        assertThat(wallet.getAddress()).isEqualTo("2NGEpb541CnfpmA3LEWoehMNCnG94ybTR3F");
        assertThat(wallet.getRedeemScript()).isEqualTo("51210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae");
        assertThat(wallet.getScriptPubKey()).isEqualTo("a914fc375c082884b9d7575ac04102d11218406434d287");
        assertThat(wallet.getUsers().size()).isEqualTo(2);
        assertThat(wallet.getMinSignatures()).isEqualTo(1);
        Transaction transaction = transactionRepository.findFirstByStatus(TransactionStatus.PENDING).orElseThrow();
        assertThat(transaction.getNotifications()).hasSize(2);
        assertThat(transaction.getNotifications()).extracting(Notification::getType).allMatch(s -> s == NotificationType.WALLET_RECREATION);
    }

    @Test
    void shouldReturn409WhenWalletInCreationAlreadyExists() throws Exception {
        Wallet oldWallet = new Wallet("123", "456", "789", Collections.emptyList(), 2);
        oldWallet.setStatus(WalletStatus.ON_DUTY);
        Wallet newWallet = new Wallet("123", "456", "789", Collections.emptyList(), 2);
        newWallet.setStatus(WalletStatus.IN_CREATION);
        walletRepository.saveAll(List.of(oldWallet, newWallet));
        List<String> adminEmails = List.of("dd@dd.pl", "ddd@dd.pl");
        CreateWalletRequest request = new CreateWalletRequest(2, adminEmails);
        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn409WhenTransactionIsInProgress() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);
        Wallet oldWallet = new Wallet("123", "456", "789", Collections.emptyList(), 2);
        oldWallet.setStatus(WalletStatus.ON_DUTY);
        walletRepository.save(oldWallet);
        List<String> adminEmails = List.of("dd@dd.pl", "ddd@dd.pl");
        CreateWalletRequest request = new CreateWalletRequest(2, adminEmails);
        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenRecreatedWaletHasTheSameData() throws Exception {
        String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        admin1.setPublicKey(publicKey);
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        admin2.setPublicKey(publicKey);
        Wallet oldWallet = new Wallet("123", "456", "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4", List.of(admin1, admin2), 2);
        oldWallet.setStatus(WalletStatus.ON_DUTY);
        walletRepository.save(oldWallet);
        admin1.setWallet(oldWallet);
        admin2.setWallet(oldWallet);
        adminUserRepository.save(admin1);
        adminUserRepository.save(admin2);
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        CreateWalletRequest request = new CreateWalletRequest(2, adminEmails);
        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenNoEmailsProvided() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(1, Collections.emptyList());

        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenNotEmailProvided() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(1, List.of("not an email"));

        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenInvalidNumberOfSignatures() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(0, List.of(EMAIL));

        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenMoreSignaturesThanEmails() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(5, List.of(EMAIL));

        mockMvc.perform(post("/api/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200ForProperChannelClose() throws Exception {
        ListChannelsResponse response = new ListChannelsResponse();
        response.setChannels(Collections.emptyList());
        when(lndAPI.listChannels(any())).thenReturn(response);
        mockMvc.perform(post("/api/wallet/closeChannels"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForTransfer() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList(), 1);
        wallet.setStatus(WalletStatus.ON_DUTY);
        walletRepository.save(wallet);
        WalletBalanceResponse balanceRequest = new WalletBalanceResponse();
        balanceRequest.setConfirmedBalance(2137L);
        SendCoinsResponse sendCoinsResponse = new SendCoinsResponse();
        sendCoinsResponse.setTxid("tx");
        when(lndAPI.walletBalance()).thenReturn(balanceRequest);
        when(lndAPI.sendCoins(any())).thenReturn(sendCoinsResponse);
        mockMvc.perform(post("/api/wallet/transfer"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForBalanceRequest() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList(), 1);
        wallet.setStatus(WalletStatus.ON_DUTY);
        walletRepository.save(wallet);
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(1000L));
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(900L));
        when(walletAppKit.wallet()).thenReturn(walletMock);
        ChannelBalanceResponse balanceResponse = new ChannelBalanceResponse();
        balanceResponse.setBalance(1000L);
        Channel channel1 = new Channel();
        channel1.setChannelPoint("channel1:1");
        channel1.setActive(true);
        channel1.setChanId(1);
        ListChannelsResponse listChannelsResponse = new ListChannelsResponse();
        listChannelsResponse.setChannels(List.of(channel1));
        when(lndAPI.channelBalance()).thenReturn(balanceResponse);
        when(lndAPI.listChannels(any())).thenReturn(listChannelsResponse);
        WalletBalanceResponse walletBalanceResponse = new WalletBalanceResponse();
        walletBalanceResponse.setUnconfirmedBalance(100L);
        walletBalanceResponse.setConfirmedBalance(1000L);
        when(lndAPI.walletBalance()).thenReturn(walletBalanceResponse);
        Instant date = Instant.ofEpochSecond(1656272934);
        Payment payment1 = new Payment("123", 1, 1, 123, PaymentStatus.COMPLETE, null);
        payment1.setDate(date);
        Payment payment2 = new Payment("125", 1, 2, 123, PaymentStatus.COMPLETE, null);
        payment2.setDate(date.minusSeconds(3_000_000));
        Payment payment3 = new Payment("124", 1, 3, 123, PaymentStatus.COMPLETE, null);
        payment3.setDate(date.minusSeconds(6_000_000));
        paymentRepository.saveAll(List.of(payment1, payment2, payment3));

        String jsonContent = getJsonResponse("integration/wallet/response/balance-GET.json");

        mockMvc.perform(get("/api/wallet"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturn404ForBalanceWhenNoWalletCreated() throws Exception {
        mockMvc.perform(get("/api/wallet"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn500WhenThereIsLightningException() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList(), 1);
        wallet.setStatus(WalletStatus.ON_DUTY);
        walletRepository.save(wallet);
        when(lndAPI.channelBalance()).thenThrow(ValidationException.class);

        mockMvc.perform(get("/api/wallet"))
                .andExpect(status().isInternalServerError());
    }

}

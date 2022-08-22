package pl.edu.pjatk.lnpayments.webservice.transaction.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.core.Coin;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.notification.repository.NotificationRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionRequest;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

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
class TransactionResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletAppKit walletAppKit;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    void shouldReturn200SaveTransaction() throws Exception{
        RegTestParams params = RegTestParams.get();
        AdminUser user = createAdminUser("test@test.pl");
        Wallet wallet = Wallet.builder()
                .users(List.of(user))
                .minSignatures(1)
                .address("2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4")
                .build();
        wallet.setStatus(WalletStatus.ON_DUTY);
        walletRepository.save(wallet);
        user.setWallet(wallet);
        userRepository.save(user);
        org.bitcoinj.core.Transaction transaction1 = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000"));
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletAppKit.wallet()).thenReturn(walletMock);
        when(walletAppKit.params()).thenReturn(params);
        when(walletMock.getWatchedOutputs(true)).thenReturn(transaction1.getOutputs());
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(10000L));
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(0L));
        TransactionRequest request = new TransactionRequest(100L, "2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv");

        mockMvc.perform(post("/api/transactions")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(1);
        Transaction transaction = transactions.get(0);
        assertThat(transaction.getNotifications()).hasSize(1);
        assertThat(transaction.getRequiredApprovals()).isEqualTo(1);
        assertThat(transaction.getDateCreated()).isBefore(Instant.now());
    }

    @Test
    void shouldReturn400WhenTransactionExists() throws Exception {
        TransactionRequest request = new TransactionRequest(100L, "2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv");
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);
        mockMvc.perform(post("/api/transactions")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenNotEnoughBalance() throws Exception {
        RegTestParams params = RegTestParams.get();
        AdminUser user = createAdminUser("test@test.pl");
        Wallet wallet = Wallet.builder()
                .users(List.of(user))
                .address("2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4")
                .build();
        wallet.setStatus(WalletStatus.ON_DUTY);
        user.setWallet(wallet);
        walletRepository.save(wallet);
        org.bitcoinj.core.Transaction transaction1 = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000"));
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletAppKit.wallet()).thenReturn(walletMock);
        when(walletAppKit.params()).thenReturn(params);
        when(walletMock.getWatchedOutputs(true)).thenReturn(transaction1.getOutputs());
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(1000L));
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(0L));
        TransactionRequest request = new TransactionRequest(10000L, "2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv");

        mockMvc.perform(post("/api/transactions")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrow400WhenInvalidAddressProvided() throws Exception {
        TransactionRequest request = new TransactionRequest(10000L, "2137");

        mockMvc.perform(post("/api/transactions")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOkAndListOfTransactions() throws Exception {
        Transaction transaction1 = Transaction.builder()
                .rawTransaction("abc")
                .sourceAddress("qwe")
                .targetAddress("asd")
                .requiredApprovals(2)
                .fee(100L)
                .inputValue(100L)
                .build();
        Transaction transaction2 = Transaction.builder()
                .rawTransaction("ewq")
                .sourceAddress("dsa")
                .targetAddress("czx")
                .requiredApprovals(2)
                .fee(101L)
                .inputValue(99L)
                .build();
        transaction2.setStatus(TransactionStatus.APPROVED);
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        String jsonContent = getJsonResponse("integration/payment/response/transactions-GET.json");

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturnOkAndListOfTransactionsWhenNoPending() throws Exception {
        Transaction transaction2 = Transaction.builder()
                .rawTransaction("ewq")
                .sourceAddress("dsa")
                .targetAddress("czx")
                .requiredApprovals(2)
                .fee(101L)
                .inputValue(99L)
                .build();
        transaction2.setStatus(TransactionStatus.APPROVED);
        transactionRepository.save(transaction2);
        String jsonContent = getJsonResponse("integration/payment/response/transactions-GET-no-pending.json");

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturnOkAndEmptyResponseWhenNoTransactions() throws Exception {
        String jsonContent = getJsonResponse("integration/payment/response/transactions-GET-empty.json");

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturnOkForNewTransactionInfoData() throws Exception {
        String jsonContent = getJsonResponse("integration/payment/response/new-transaction-info-GET.json");
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletAppKit.wallet()).thenReturn(walletMock);
        when(walletAppKit.params()).thenReturn(TestNet3Params.get());
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(1000L));
        when(walletMock.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(0L));

        mockMvc.perform(get("/api/transactions/info"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

}

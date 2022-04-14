package pl.edu.pjatk.lnpayments.webservice.wallet.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.core.Coin;
import org.bitcoinj.kits.WalletAppKit;
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
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.CreateWalletRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
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

    @AfterEach
    void tearDown() {
        walletRepository.deleteAll();
        adminUserRepository.deleteAll();
    }

    @Test
    void shouldReturnCreatedWhenNoIssues() throws Exception {
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        CreateWalletRequest request = new CreateWalletRequest(1, adminEmails);
        adminUserRepository.save(admin1);
        adminUserRepository.save(admin2);

        mockMvc.perform(post("/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<Wallet> wallets = walletRepository.findAll();
        assertThat(wallets.size()).isEqualTo(1);
        assertThat(wallets.get(0).getAddress()).isEqualTo("2NGEpb541CnfpmA3LEWoehMNCnG94ybTR3F");
        assertThat(wallets.get(0).getRedeemScript()).isEqualTo("51210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae");
        assertThat(wallets.get(0).getScriptPubKey()).isEqualTo("a914fc375c082884b9d7575ac04102d11218406434d287");
        assertThat(wallets.get(0).getUsers().size()).isEqualTo(2);
    }

    @Test
    void shouldReturn409IfWalletAlreadyExists() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList());
        walletRepository.save(wallet);
        CreateWalletRequest request = new CreateWalletRequest(1, List.of(EMAIL));

        mockMvc.perform(post("/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenWrongNumberOfAdmins() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(1, List.of(EMAIL));

        mockMvc.perform(post("/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenNoEmailsProvided() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(1, Collections.emptyList());

        mockMvc.perform(post("/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenNotEmailProvided() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(1, List.of("not an email"));

        mockMvc.perform(post("/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenInvalidNumberOfSignatures() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(0, List.of(EMAIL));

        mockMvc.perform(post("/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenMoreSignaturesThanEmails() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(5, List.of(EMAIL));

        mockMvc.perform(post("/wallet")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200ForProperChannelClose() throws Exception {
        ListChannelsResponse response = new ListChannelsResponse();
        response.setChannels(Collections.emptyList());
        when(lndAPI.listChannels(any())).thenReturn(response);
        mockMvc.perform(post("/wallet/closeChannels"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForTransfer() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList());
        walletRepository.save(wallet);
        WalletBalanceResponse balanceRequest = new WalletBalanceResponse();
        balanceRequest.setConfirmedBalance(2137L);
        SendCoinsResponse sendCoinsResponse = new SendCoinsResponse();
        sendCoinsResponse.setTxid("tx");
        when(lndAPI.walletBalance()).thenReturn(balanceRequest);
        when(lndAPI.sendCoins(any())).thenReturn(sendCoinsResponse);
        mockMvc.perform(post("/wallet/transfer"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForBalanceRequest() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList());
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

        String jsonContent = getJsonResponse("integration/wallet/response/balance-GET.json");

        mockMvc.perform(get("/wallet"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturn404ForBalanceWhenNoWalletCreated() throws Exception {
        mockMvc.perform(get("/wallet"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn500WhenThereIsLightningException() throws Exception {
        Wallet wallet = new Wallet("123", "456", "789", Collections.emptyList());
        walletRepository.save(wallet);
        when(lndAPI.channelBalance()).thenThrow(ValidationException.class);

        mockMvc.perform(get("/wallet"))
                .andExpect(status().isInternalServerError());
    }

}

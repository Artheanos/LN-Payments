package pl.edu.pjatk.lnpayments.webservice.wallet.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

}

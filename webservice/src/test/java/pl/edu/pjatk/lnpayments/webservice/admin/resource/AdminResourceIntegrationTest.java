package pl.edu.pjatk.lnpayments.webservice.admin.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminDeleteRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.KeyUploadRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class AdminResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @MockBean
    private Principal principal;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        adminUserRepository.deleteAll();
    }

    @Test
    void shouldReturn201AndSaveAdmin() throws Exception {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "zaq1@WSX");
        mockMvc.perform(post("/api/admins")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertThat(adminUserRepository.existsByEmail("test@test.pl")).isTrue();
    }

    @Test
    void shouldReturn409WhenEmailIsTaken() throws Exception {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "zaq1@WSX");
        AdminUser user = AdminUser.adminBuilder().email("test@test.pl").build();
        adminUserRepository.save(user);
        mockMvc.perform(post("/api/admins")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenInputIsInvalid() throws Exception {
        AdminRequest request = new AdminRequest(null, null, "");
        mockMvc.perform(post("/api/admins")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200AndSavedAdmins() throws Exception {
        AdminUser firstAdmin = AdminUser.adminBuilder().email("test1@test.pl").build();
        AdminUser secondAdmin = AdminUser.adminBuilder().email("test2@test.pl").build();
        StandardUser standardUser = new StandardUser("dd@dd.pl", "standard", "zaq1@WSX");
        userRepository.save(standardUser);
        adminUserRepository.save(firstAdmin);
        adminUserRepository.save(secondAdmin);
        String jsonContent = getJsonResponse("integration/payment/response/admins-GET-valid.json");
        mockMvc.perform(get("/api/admins"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturn200AndEmptyListWhenNoUsers() throws Exception {
        String jsonContent = getJsonResponse("integration/payment/response/admins-GET-empty.json");
        mockMvc.perform(get("/api/admins"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturn404WhenAdminNotFound() throws Exception {
        AdminDeleteRequest deleteRequest = new AdminDeleteRequest("test@test.pl");

        mockMvc.perform(delete("/api/admins")
                .content(new ObjectMapper().writeValueAsString(deleteRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        assertThat(adminUserRepository.existsByEmail("test@test.pl")).isFalse();
    }

    @Test
    void shouldReturn200WhenAdminDeletedCorrectly() throws Exception {
        AdminUser admin = AdminUser.adminBuilder().email("test@test.pl").build();
        AdminDeleteRequest deleteRequest = new AdminDeleteRequest("test@test.pl");

        adminUserRepository.save(admin);

        mockMvc.perform(delete("/api/admins")
                        .content(new ObjectMapper().writeValueAsString(deleteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertThat(adminUserRepository.existsByEmail("test@test.pl")).isFalse();
    }

    @Test
    void shouldReturn409WhenAdminIsAddedToWallet() throws Exception {
        AdminUser admin = AdminUser.adminBuilder().email("test@test.pl").build();
        AdminDeleteRequest deleteRequest = new AdminDeleteRequest("test@test.pl");
        Wallet wallet = new Wallet();

        walletRepository.save(wallet);
        admin.setWallet(wallet);
        adminUserRepository.save(admin);

        mockMvc.perform(delete("/api/admins")
                        .content(new ObjectMapper().writeValueAsString(deleteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        assertThat(adminUserRepository.existsByEmail("test@test.pl")).isTrue();
    }

    @Nested
    class UploadKeyTest {

        private static final String EMAIL = "test1@test.pl";

        @BeforeEach
        void setUp() {
            when(principal.getName()).thenReturn(EMAIL);
        }

        @Test
        void shouldReturn200WhenValidKeyUploadPerformed() throws Exception {
            String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";
            AdminUser admin = AdminUser.adminBuilder().email(EMAIL).build();
            adminUserRepository.save(admin);
            KeyUploadRequest keyUploadRequest = new KeyUploadRequest(publicKey);

            mockMvc.perform(patch("/api/admins/keys")
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(keyUploadRequest)))
                    .andExpect(status().isOk());
            assertThat(adminUserRepository.findByEmail(EMAIL).orElseThrow().getPublicKey()).isEqualTo(publicKey);
        }

        @Test
        void shouldReturn400WhenInvalidKeyProvided() throws Exception {
            String publicKey = "2137";
            KeyUploadRequest keyUploadRequest = new KeyUploadRequest(publicKey);

            mockMvc.perform(patch("/api/admins/keys")
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(keyUploadRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenKeyNotProvided() throws Exception {
            KeyUploadRequest keyUploadRequest = new KeyUploadRequest(null);

            mockMvc.perform(patch("/api/admins/keys")
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(keyUploadRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn409WhenUserAlreadyHasKey() throws Exception {
            String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";
            AdminUser admin = AdminUser.adminBuilder().email(EMAIL).build();
            admin.setPublicKey(publicKey);
            adminUserRepository.save(admin);
            KeyUploadRequest keyUploadRequest = new KeyUploadRequest(publicKey);

            mockMvc.perform(patch("/api/admins/keys")
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(keyUploadRequest)))
                    .andExpect(status().isConflict());
        }

        @Test
        void shouldReturn404WhenUserDoesNotExist() throws Exception {
            String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";
            KeyUploadRequest keyUploadRequest = new KeyUploadRequest(publicKey);

            mockMvc.perform(patch("/api/admins/keys")
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(keyUploadRequest)))
                    .andExpect(status().isNotFound());
        }
    }

}

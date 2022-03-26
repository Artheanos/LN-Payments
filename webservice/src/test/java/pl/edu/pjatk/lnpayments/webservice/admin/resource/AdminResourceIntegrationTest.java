package pl.edu.pjatk.lnpayments.webservice.admin.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturn201AndSaveAdmin() throws Exception {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "zaq1@WSX");
        mockMvc.perform(post("/admins")
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
        mockMvc.perform(post("/admins")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenInputIsInvalid() throws Exception {
        AdminRequest request = new AdminRequest(null, null, "");
        mockMvc.perform(post("/admins")
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
        mockMvc.perform(get("/admins"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturn200AndEmptyListWhenNoUsers() throws Exception {
        String jsonContent = getJsonResponse("integration/payment/response/admins-GET-empty.json");
        mockMvc.perform(get("/admins"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }
}

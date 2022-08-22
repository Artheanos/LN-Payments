package pl.edu.pjatk.lnpayments.webservice.common.resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.StandardUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;

import java.security.Principal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class UserResourceIntegrationTest extends BaseIntegrationTest {

    private StandardUser user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StandardUserRepository userRepository;

    @MockBean
    private Principal principal;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        user = new StandardUser("test@test.pl", "asd", passwordEncoder.encode("asd"));
        when(principal.getName()).thenReturn(user.getEmail());
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturn200AndUserDetails() throws Exception {
        String jsonContent = getJsonResponse("integration/common/response/users-GET.json");
        mockMvc.perform(get("/api/users")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users")
                        .principal(() -> "dudu@dudu.pl"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200AndUpdatePasswordForProperData() throws Exception {
        String request = getJsonResponse("integration/common/request/password-PUT.json");
        mockMvc.perform(put("/api/users/password")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk());

        StandardUser standardUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(passwordEncoder.matches("zaq1@WSX", standardUser.getPassword())).isTrue();
    }

    @Test
    void shouldReturn404WhenUserToChangePasswordNotFound() throws Exception {
        String request = getJsonResponse("integration/common/request/password-PUT.json");
        mockMvc.perform(put("/api/users/password")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(() -> "not found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenPasswordDoesNotMatch() throws Exception {
        String request = getJsonResponse("integration/common/request/password-PUT-wrong-current.json");
        mockMvc.perform(put("/api/users/password")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isConflict());
    }

}

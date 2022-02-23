package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;
import pl.edu.pjatk.lnpayments.webservice.payment.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.payment.helper.config.IntegrationTestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class AuthResourceIntegrationTest extends BaseIntegrationTest {

    private static final String EMAIL = "test@test.pl";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnOkAndProperResponse() throws Exception {
        RegisterRequest request = new RegisterRequest(EMAIL, "test", "zaq1@WSX");
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertThat(userRepository.existsByEmail(EMAIL)).isTrue();
    }

    @Test
    void shouldReturn409WhenEmailIsTaken() throws Exception {
        RegisterRequest request = new RegisterRequest(EMAIL, "test", "zaq1@WSX");
        User user = User.builder().email(EMAIL).build();
        userRepository.save(user);
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400ForInvalidParams() throws Exception {
        RegisterRequest request = new RegisterRequest(EMAIL, null, "");
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginAndReturnOkIfUserExists() throws Exception {
        LoginRequest request = new LoginRequest(EMAIL, "test");
        User user = createTestUser();
        userRepository.save(user);

        String response = mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        assertThat(loginResponse.getFullName()).isEqualTo("test");
        assertThat(loginResponse.getEmail()).isEqualTo(EMAIL);
        assertThat(loginResponse.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    void shouldReturn401WhenPasswordDoesNotMatch() throws Exception {
        LoginRequest request = new LoginRequest(EMAIL, "lololol");
        User user = createTestUser();
        userRepository.save(user);

        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void shouldReturn401WhenUserDoesNotExist() throws Exception {
        LoginRequest request = new LoginRequest(EMAIL, "test");

        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION));
    }

    private User createTestUser() {
        return User.builder()
                .email(EMAIL)
                .role(Role.ROLE_USER)
                .password(passwordEncoder.encode("test"))
                .fullName("test").build();
    }
}

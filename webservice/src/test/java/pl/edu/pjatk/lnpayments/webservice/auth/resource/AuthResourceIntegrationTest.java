package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Condition;
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
import org.springframework.test.web.servlet.MvcResult;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RefreshTokenResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private JwtService jwtService;

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
        StandardUser user = StandardUser.builder().email(EMAIL).build();
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
        StandardUser user = createTestUser();
        userRepository.save(user);

        String response = mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        assertThat(loginResponse.getFullName()).isEqualTo("test");
        assertThat(loginResponse.getEmail()).isEqualTo(EMAIL);
        assertThat(loginResponse.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(loginResponse.getToken()).is(new Condition<>(jwtService::isTokenValid, "Is JWT token?"));
    }

    @Test
    void shouldReturn401WhenPasswordDoesNotMatch() throws Exception {
        LoginRequest request = new LoginRequest(EMAIL, "lololol");
        StandardUser user = createTestUser();
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

    private StandardUser createTestUser() {
        return StandardUser.builder()
                .email(EMAIL)
                .password(passwordEncoder.encode("test"))
                .fullName("test").build();
    }

    @Test
    void refreshTokenShouldReturnOkAndNewToken() throws Exception {
        String email = "email@email.com";
        userRepository.save(new StandardUser(email, "", ""));
        String token = jwtService.generateToken(email);

        MvcResult result = mockMvc
                .perform(get("/auth/refreshToken").header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)))
                .andExpect(status().isOk())
                .andReturn();

        String stringResponse = result.getResponse().getContentAsString();
        RefreshTokenResponse response = objectMapper.readValue(stringResponse, RefreshTokenResponse.class);
        String responseToken = response.getToken();
        assertThat(jwtService.retrieveEmail(responseToken)).isEqualTo(email);
    }

    @Test
    void refreshTokenShouldReturn401WhenTheTokenIsInvalid() throws Exception {
        String email = "email@email.com";
        userRepository.save(new StandardUser(email, "", ""));
        String token = "thisTokenIsInvalid";

        mockMvc
                .perform(get("/auth/refreshToken").header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)))
                .andExpect(status().isUnauthorized());
    }
}

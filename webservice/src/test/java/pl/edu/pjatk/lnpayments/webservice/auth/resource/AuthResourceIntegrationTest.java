package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RefreshTokenResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;
import pl.edu.pjatk.lnpayments.webservice.payment.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.payment.helper.config.IntegrationTestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class AuthResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void registrationShouldReturnOkAndProperResponse() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "zaq1@WSX");
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertThat(userRepository.existsByEmail("test@test.pl")).isTrue();
    }

    @Test
    void registrationShouldReturn409WhenEmailIsTaken() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "zaq1@WSX");
        User user = User.builder().email("test@test.pl").build();
        userRepository.save(user);
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void registrationShouldReturn400ForInvalidParams() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.pl", null, "");
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshTokenShouldReturnOkAndNewToken() throws Exception {
        String email = "email@email.com";
        userRepository.save(new User(email, "", "", Role.ROLE_USER));
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
        userRepository.save(new User(email, "", "", Role.ROLE_USER));
        String token = jwtService.generateToken(email) + "1";

        mockMvc
                .perform(get("/auth/refreshToken").header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token)))
                .andExpect(status().isUnauthorized());
    }
}

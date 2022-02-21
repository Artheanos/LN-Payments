package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;
import pl.edu.pjatk.lnpayments.webservice.payment.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.payment.helper.config.IntegrationTestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
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

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnOkAndProperResponse() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "zaq1@WSX");
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertThat(userRepository.existsByEmail("test@test.pl")).isTrue();
    }

    @Test
    void shouldReturn409WhenEmailIsTaken() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "zaq1@WSX");
        User user = User.builder().email("test@test.pl").build();
        userRepository.save(user);
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400ForInvalidParams() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.pl", null, "");
        mockMvc.perform(post("/auth/register")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

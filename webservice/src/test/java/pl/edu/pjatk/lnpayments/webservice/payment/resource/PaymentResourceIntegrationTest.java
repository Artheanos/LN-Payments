package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.test.web.servlet.MvcResult;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.PaymentRepository;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createStandardUser;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class PaymentResourceIntegrationTest extends BaseIntegrationTest {

    private final static String EMAIL = "test@test.pl";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Principal principal;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        when(principal.getName()).thenReturn(EMAIL);
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    class PaymentInfoIntegrationTest {

        @Test
        void shouldReturnOkAndCorrectResponse() throws Exception {
            userRepository.save(createStandardUser(EMAIL));
            String jsonContent = getJsonResponse("integration/payment/response/profileinfo-GET-valid.json");
            mockMvc.perform(get("/payments/info")
                            .principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(content().json(jsonContent));
        }

        @Test
        void shouldReturnOkWhenUserNotAuthorized() throws Exception {
            when(principal.getName()).thenReturn(null);
            String jsonContent = getJsonResponse("integration/payment/response/profileinfo-GET-valid.json");
            mockMvc.perform(get("/payments/info")
                            .principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(content().json(jsonContent));
        }

        @Test
        void shouldReturnOkWhenNoPrincipalGiven() throws Exception {
            String jsonContent = getJsonResponse("integration/payment/response/profileinfo-GET-valid.json");
            mockMvc.perform(get("/payments/info"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(jsonContent));
        }
    }

    @Nested
    class PaymentDetailsIntegrationTest {

        @Test
        void shouldReturnOkForProperRequest() throws Exception {
            userRepository.save(createStandardUser(EMAIL));
            String request = getJsonResponse("integration/payment/request/details-POST-valid.json");
            MvcResult mvcResult = mockMvc.perform(post("/payments")
                            .principal(principal)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            String paymentRequest = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.paymentRequest");
            assertThat(paymentRequest).isEqualTo("invoice");
        }

        @Test
        void shouldReturn404WhenInvalidTokenNumberProvided() throws Exception {
            PaymentDetailsRequest request = new PaymentDetailsRequest(-1);
            mockMvc.perform(post("/payments")
                            .content(new ObjectMapper().writeValueAsString(request))
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}

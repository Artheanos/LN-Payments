package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class PaymentResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class PaymentInfoIntegrationTest {

        @Test
        void shouldReturnOkAndCorrectResponse() throws Exception {
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
            String request = getJsonResponse("integration/payment/request/details-POST-valid.json");
            MvcResult mvcResult = mockMvc.perform(post("/payments")
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            String paymentRequest = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.paymentRequest");
            assertThat(paymentRequest).isEqualTo("invoice");
        }

        @Test
        void shouldReturn404WhenInvalidTokenNumberProvided() throws Exception {
            PaymentDetailsRequest request = new PaymentDetailsRequest(-1, "test@test.pl");
            mockMvc.perform(post("/payments")
                            .content(new ObjectMapper().writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnOkWhenNoEmail() throws Exception {
            PaymentDetailsRequest request = new PaymentDetailsRequest(1, null);
            mockMvc.perform(post("/payments")
                            .content(new ObjectMapper().writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

    }


}

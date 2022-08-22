package pl.edu.pjatk.lnpayments.webservice.common.resource;

import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjatk.lnpayments.webservice.helper.SettingsHelper;
import pl.edu.pjatk.lnpayments.webservice.helper.config.BaseIntegrationTest;
import pl.edu.pjatk.lnpayments.webservice.helper.config.IntegrationTestConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class ConfigurationResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Configuration configuration;

    @AfterEach
    void tearDown() {
        SettingsHelper.resetSettings(configuration);
    }

    @Test
    void shouldReturn200GetSettings() throws Exception {
        String jsonResponse = getJsonResponse("integration/settings/response/settings-GET.json");

        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldReturn200AndSave() throws Exception {
        String jsonRequest = getJsonResponse("integration/settings/request/settings-PUT-valid.json");

        mockMvc.perform(put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        assertThat(configuration.getInt("price")).isEqualTo(2137);
        assertThat(configuration.getLong("lastModification")).isGreaterThan(1);
    }

    @Test
    void shouldReturn412AndSaveWhenUpdatedBefore() throws Exception {
        String jsonRequest = getJsonResponse("integration/settings/request/settings-PUT-wrong-version.json");

        mockMvc.perform(put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    void shouldReturn400WhenInputsAreInvalid() throws Exception {
        String jsonRequest = getJsonResponse("integration/settings/request/settings-PUT-invalid.json");

        mockMvc.perform(put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

}

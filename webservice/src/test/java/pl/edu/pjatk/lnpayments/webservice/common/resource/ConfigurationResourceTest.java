package pl.edu.pjatk.lnpayments.webservice.common.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PropertyValues;
import pl.edu.pjatk.lnpayments.webservice.common.service.SettingsService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationResourceTest {

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private ConfigurationResource configurationResource;

    @Test
    void shouldReturnOkForConfigRetrieval() {
        PropertyValues values = new PropertyValues();
        values.setPrice(2137);
        when(settingsService.getSettings()).thenReturn(values);

        ResponseEntity<PropertyValues> response = configurationResource.getSettings();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(settingsService).getSettings();
    }

    @Test
    void shouldReturnOkForSettingsSave() {
        PropertyValues values = new PropertyValues();
        values.setPrice(2137);

        ResponseEntity<?> response = configurationResource.updateSettings(values);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(settingsService).saveSettings(values);
    }
}

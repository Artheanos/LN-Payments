package pl.edu.pjatk.lnpayments.webservice.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PropertyValues;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock
    private PropertiesConfiguration propertiesConfiguration;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SettingsService settingsService;

    @Test
    @SuppressWarnings("unchecked")
    void shouldGetSettings() {
        PropertyValues values = new PropertyValues();
        values.setPrice(100);
        when(propertiesConfiguration.getProperty("price")).thenReturn(100);
        when(propertiesConfiguration.getKeys()).thenReturn(List.of("price").iterator());
        when(objectMapper.convertValue(eq(values), any(TypeReference.class))).thenReturn(values);

        PropertyValues settings = settingsService.getSettings();

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(objectMapper.convertValue(captor.capture(), any(TypeReference.class)));
        assertThat(values.getPrice()).isEqualTo(settings.getPrice());
        assertThat(values.getPrice()).isEqualTo(captor.getValue().get("price"));
    }
}

package pl.edu.pjatk.lnpayments.webservice.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PropertyValues;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock
    private Configuration propertiesConfiguration;

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
        when(objectMapper.convertValue(any(), eq(PropertyValues.class))).thenReturn(values);

        PropertyValues settings = settingsService.getSettings();

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(objectMapper).convertValue(captor.capture(), eq(PropertyValues.class));
        assertThat(values.getPrice()).isEqualTo(settings.getPrice());
        assertThat(values.getPrice()).isEqualTo(captor.getValue().get("price"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSaveSettingsIfCorrectVersionProvided() {
        PropertyValues values = new PropertyValues();
        values.setPrice(100);
        values.setLastModification(1L);
        when(propertiesConfiguration.getLong("lastModification")).thenReturn(1L);
        Map<String, String> testMap = Map.of("testK", "testV");
        when(objectMapper.convertValue(any(PropertyValues.class), any(TypeReference.class))).thenReturn(testMap);

        settingsService.saveSettings(values);

        ArgumentCaptor<PropertyValues> captor = ArgumentCaptor.forClass(PropertyValues.class);
        verify(propertiesConfiguration).setProperty("testK", testMap.get("testK"));
        verify(objectMapper).convertValue(captor.capture(), any(TypeReference.class));
        PropertyValues capturedValues = captor.getValue();
        assertThat(capturedValues.getLastModification()).isGreaterThan(1L);
    }

    @Test
    void shouldGetPrice() {
        int initialValue = 100;
        when(propertiesConfiguration.getInt("price")).thenReturn(initialValue);

        int value = settingsService.getPrice();

        assertThat(value).isEqualTo(initialValue);
    }

    @Test
    void shouldGetDescription() {
        String initialValue = "dududu";
        when(propertiesConfiguration.getString("description")).thenReturn(initialValue);

        String value = settingsService.getDescription();

        assertThat(value).isEqualTo(initialValue);
    }

    @Test
    void shouldGetInvoiceMemo() {
        String initialValue = "dududu";
        when(propertiesConfiguration.getString("invoiceMemo")).thenReturn(initialValue);

        String value = settingsService.getInvoiceMemo();

        assertThat(value).isEqualTo(initialValue);
    }

    @Test
    void shouldGetPaymentExpiryInSeconds() {
        int initialValue = 100;
        when(propertiesConfiguration.getInt("paymentExpiryInSeconds")).thenReturn(initialValue);

        int value = settingsService.getPaymentExpiryInSeconds();

        assertThat(value).isEqualTo(initialValue);
    }

    @Test
    void shouldGetAutoChannelCloseLimit() {
        long initialValue = 100L;
        when(propertiesConfiguration.getLong("autoChannelCloseLimit")).thenReturn(initialValue);

        long value = settingsService.getAutoChannelCloseLimit();

        assertThat(value).isEqualTo(initialValue);
    }

    @Test
    void shouldGetAutoTransferLimit() {
        long initialValue = 100L;
        when(propertiesConfiguration.getLong("autoTransferLimit")).thenReturn(initialValue);

        long value = settingsService.getAutoTransferLimit();

        assertThat(value).isEqualTo(initialValue);
    }

    @Test
    void shouldGetTokenDeliveryUrl() {
        String initialValue = "https://127.0.0.1";
        when(propertiesConfiguration.getString("tokenDeliveryUrl")).thenReturn(initialValue);

        String value = settingsService.getTokenDeliveryUrl();

        assertThat(value).isEqualTo(initialValue);
    }

    @Test
    void shouldGetIpAddress() {
        String initialValue = "127.0.0.1";
        when(propertiesConfiguration.getString("serverIpAddress")).thenReturn(initialValue);

        String value = settingsService.getServerIpAddress();

        assertThat(value).isEqualTo(initialValue);
    }
}

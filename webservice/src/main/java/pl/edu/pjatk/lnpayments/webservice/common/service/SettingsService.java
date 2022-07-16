package pl.edu.pjatk.lnpayments.webservice.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration2.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PropertyValues;

import javax.persistence.OptimisticLockException;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SettingsService implements PropertyService {

    private static final TypeReference<Map<String, Object>> PROPS_TYPE_REF = new TypeReference<>() {};

    private final Configuration propertiesConfiguration;
    private final ObjectMapper objectMapper;

    @Autowired
    public SettingsService(Configuration propertiesConfiguration, ObjectMapper objectMapper) {
        this.propertiesConfiguration = propertiesConfiguration;
        this.objectMapper = objectMapper;
    }

    public PropertyValues getSettings() {
        Map<String, Object> props = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(propertiesConfiguration.getKeys(), Spliterator.ORDERED),
                        false)
                .collect(Collectors.toMap(Function.identity(), propertiesConfiguration::getProperty));
        return objectMapper.convertValue(props, PropertyValues.class);
    }

    public void saveSettings(PropertyValues values) {
        if (values.getLastModification() != propertiesConfiguration.getLong("lastModification")) {
            throw new OptimisticLockException("Settings have already been modified");
        }
        values.setLastModification(System.currentTimeMillis());
        objectMapper.convertValue(values, PROPS_TYPE_REF)
                .forEach(propertiesConfiguration::setProperty);
    }

    @Override
    public int getPrice() {
        return propertiesConfiguration.getInt("price");
    }

    @Override
    public String getDescription() {
        return propertiesConfiguration.getString("description");
    }

    @Override
    public String getInvoiceMemo() {
        return propertiesConfiguration.getString("invoiceMemo");
    }

    @Override
    public int getPaymentExpiryInSeconds() {
        return propertiesConfiguration.getInt("paymentExpiryInSeconds");
    }

    @Override
    public long getAutoChannelCloseLimit() {
        return propertiesConfiguration.getLong("autoChannelCloseLimit");
    }

    @Override
    public long getAutoTransferLimit() {
        return propertiesConfiguration.getLong("autoTransferLimit");
    }

    @Override
    public String getTokenDeliveryUrl() { return propertiesConfiguration.getString("tokenDeliveryUrl"); }

    @Override
    public String getServerIpAddress() {
        return propertiesConfiguration.getString("serverIpAddress");
    }
}

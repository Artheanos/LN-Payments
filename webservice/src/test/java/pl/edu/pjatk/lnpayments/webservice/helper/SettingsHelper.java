package pl.edu.pjatk.lnpayments.webservice.helper;

import org.apache.commons.configuration2.Configuration;

import java.util.AbstractMap;
import java.util.Map;

public class SettingsHelper {

    private SettingsHelper() {
    }

    public static void resetSettings(Configuration configuration) {
        configuration.clear();
        Map<String, Object> params = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("price", 100),
                new AbstractMap.SimpleEntry<>("description", "Super opis"),
                new AbstractMap.SimpleEntry<>("invoiceMemo", "memo"),
                new AbstractMap.SimpleEntry<>("paymentExpiryInSeconds", 900),
                new AbstractMap.SimpleEntry<>("autoChannelCloseLimit", 100000L),
                new AbstractMap.SimpleEntry<>("autoTransferLimit", 100000L),
                new AbstractMap.SimpleEntry<>("lastModification", 1),
                new AbstractMap.SimpleEntry<>("tokenDeliveryUrl", "http://localhost:9000/tokens"));
        configuration.clear();
        params.forEach(configuration::setProperty);
    }
}

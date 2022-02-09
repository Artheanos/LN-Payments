package pl.edu.pjatk.lnpayments.webservice.common.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
//TODO: Replace with service for retrieving these settings from persistent storage
public class TempSettings implements PropertyService {

    private final int price = 100;
    private final String description = "Super opis";
    private final String invoiceMemo = "Super memo";
    private final int paymentExpiryInSeconds = 900;

}

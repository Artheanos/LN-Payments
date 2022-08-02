package pl.edu.pjatk.lnpayments.webservice.common.resource.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class PropertyValues {

    @Min(1)
    private int price;
    @Size(min = 10, max=256)
    private String description;
    @Size(min = 10, max=128)
    private String invoiceMemo;
    @Min(300)
    @Max(3600)
    private int paymentExpiryInSeconds;
    @Min(100)
    private long autoChannelCloseLimit;
    @Min(100)
    private long autoTransferLimit;
    private long lastModification;
    private String tokenDeliveryUrl;
    private String serverIpAddress;
}

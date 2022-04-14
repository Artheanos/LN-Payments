package pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LightningWalletBalance {

    private long availableBalance;
    private long unconfirmedBalance;
    private long autoTransferLimit;
}

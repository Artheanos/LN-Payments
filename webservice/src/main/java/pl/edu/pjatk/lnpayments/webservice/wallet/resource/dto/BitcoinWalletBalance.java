package pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BitcoinWalletBalance {

    private long availableBalance;
    private long unconfirmedBalance;
    private long currentReferenceFee;
}

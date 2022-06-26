package pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto;

import lombok.Builder;
import lombok.Getter;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class WalletDetails {

    private final String address;
    private final List<AdminResponse> admins;
    private final ChannelsBalance channelsBalance;
    private final LightningWalletBalance lightningWalletBalance;
    private final BitcoinWalletBalance bitcoinWalletBalance;
    private final Map<Temporal, Long> totalIncomeData;
}

package pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.BitcoinWalletBalance;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewTransactionDetails {

    private BitcoinWalletBalance bitcoinWalletBalance;
    private boolean pendingExists;
}

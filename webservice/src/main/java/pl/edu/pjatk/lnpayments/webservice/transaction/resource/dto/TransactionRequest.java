package pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.common.validation.BitcoinAddress;

import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @Min(1)
    private long amount;

    @BitcoinAddress
    private String targetAddress;

}

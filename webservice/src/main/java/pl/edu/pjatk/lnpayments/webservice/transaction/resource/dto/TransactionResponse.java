package pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private TransactionDetails pendingTransaction;
    private Page<TransactionDetails> transactions;
}

package pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;

import java.time.Instant;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetails {

    private String sourceAddress;
    private String targetAddress;
    private long value;
    private long approvals;
    private int requiredApprovals;
    private TransactionStatus status;
    private Instant dateCreated;
}

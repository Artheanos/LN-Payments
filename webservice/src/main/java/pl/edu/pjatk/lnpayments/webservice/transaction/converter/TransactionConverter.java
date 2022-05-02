package pl.edu.pjatk.lnpayments.webservice.transaction.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionDetails;

@Service
public class TransactionConverter {

    public TransactionDetails convertToDto(Transaction transaction) {
        long approvalsCount = transaction.getNotifications()
                .stream()
                .filter(notification -> notification.getStatus() == NotificationStatus.CONFIRMED)
                .count();

        return TransactionDetails.builder()
                .status(transaction.getStatus())
                .approvals(approvalsCount)
                .requiredApprovals(transaction.getRequiredApprovals())
                .sourceAddress(transaction.getSourceAddress())
                .targetAddress(transaction.getTargetAddress())
                .dateCreated(transaction.getDateCreated())
                .value(transaction.getInputValue() + transaction.getFee())
                .build();
    }

    public Page<TransactionDetails> convertAllToDto(Page<Transaction> transactions) {
        return transactions.map(this::convertToDto);
    }
}

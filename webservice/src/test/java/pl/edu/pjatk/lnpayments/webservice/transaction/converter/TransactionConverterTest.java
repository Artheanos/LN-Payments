package pl.edu.pjatk.lnpayments.webservice.transaction.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionDetails;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createAdminUser;

class TransactionConverterTest {

    private final TransactionConverter transactionConverter = new TransactionConverter();

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = Transaction.builder()
                .rawTransaction("abc")
                .sourceAddress("qwe")
                .targetAddress("asd")
                .requiredApprovals(2)
                .fee(100L)
                .inputValue(100L)
                .build();
        transaction.setId(1L);
        AdminUser admin = createAdminUser("test@test.pl");
        admin.setId(1L);
        transaction.setNotifications(Collections.singletonList(
                new Notification(admin, transaction, "test", NotificationType.TRANSACTION))
        );
    }

    @Test
    void shouldConvertToDto() {
        TransactionDetails details = transactionConverter.convertToDto(transaction);

        assertThat(details.getDateCreated()).isEqualTo(transaction.getDateCreated());
        assertThat(details.getStatus()).isEqualTo(transaction.getStatus());
        assertThat(details.getSourceAddress()).isEqualTo(transaction.getSourceAddress());
        assertThat(details.getTargetAddress()).isEqualTo(transaction.getTargetAddress());
        assertThat(details.getValue()).isEqualTo(transaction.getFee() + transaction.getInputValue());
        assertThat(details.getApprovals()).isEqualTo(0);
        assertThat(details.getRequiredApprovals()).isEqualTo(transaction.getRequiredApprovals());
    }

    @Test
    void shouldConvertAll() {
        Page<Transaction> transactions = new PageImpl<>(List.of(transaction, transaction, transaction));

        Page<TransactionDetails> details = transactionConverter.convertAllToDto(transactions);

        assertThat(details.getTotalElements()).isEqualTo(3);
        assertThat(details.getContent()).extracting(TransactionDetails::getTargetAddress)
                .containsAll(transactions.getContent()
                        .stream()
                        .map(Transaction::getTargetAddress)
                        .collect(Collectors.toList()));
    }
}

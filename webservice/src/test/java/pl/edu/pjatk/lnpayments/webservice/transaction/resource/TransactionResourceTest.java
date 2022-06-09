package pl.edu.pjatk.lnpayments.webservice.transaction.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionRequest;
import pl.edu.pjatk.lnpayments.webservice.transaction.service.TransactionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionResourceTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionResource transactionResource;

    @Test
    void shouldReturnCreatedForTransactionRequest() {
        TransactionRequest request = new TransactionRequest(100L, "addr");

        ResponseEntity<?> transaction = transactionResource.createTransaction(request);

        assertThat(transaction.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(transactionService).createTransaction(request.getTargetAddress(), request.getAmount());
    }

    @Test
    void shouldReturnOkForTransactionsGet() {
        ResponseEntity<?> response = transactionResource.getTransactions(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(transactionService).findTransactions(null);
    }

    @Test
    void shouldReturnOkForNewTransactionsGetResponse() {
        ResponseEntity<?> response = transactionResource.getNewTransactionDetails();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(transactionService).getNewTransactionDetails();
    }
}

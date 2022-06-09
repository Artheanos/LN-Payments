package pl.edu.pjatk.lnpayments.webservice.transaction.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction_;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.NewTransactionDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionRequest;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionResponse;
import pl.edu.pjatk.lnpayments.webservice.transaction.service.TransactionService;

import javax.validation.Valid;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.INFO_PATH;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.TRANSACTIONS_PATH;

@RestController
@RequestMapping(TRANSACTIONS_PATH)
class TransactionResource {

    private final TransactionService transactionService;

    @Autowired
    TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    ResponseEntity<?> createTransaction(@RequestBody @Valid TransactionRequest request) {
        transactionService.createTransaction(request.getTargetAddress(), request.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(INFO_PATH)
    ResponseEntity<NewTransactionDetails> getNewTransactionDetails() {
        NewTransactionDetails newTransactionDetails = transactionService.getNewTransactionDetails();
        return ResponseEntity.ok(newTransactionDetails);
    }

    @GetMapping
    ResponseEntity<TransactionResponse> getTransactions(
            @SortDefault(sort = Transaction_.DATE_CREATED, direction = Sort.Direction.DESC) Pageable pageable) {
        TransactionResponse response = transactionService.findTransactions(pageable);
        return ResponseEntity.ok(response);
    }
}

package pl.edu.pjatk.lnpayments.webservice.transaction.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionRequest;
import pl.edu.pjatk.lnpayments.webservice.transaction.service.TransactionService;

import javax.validation.Valid;

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
        transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.INFO_PATH;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.PAYMENTS_PATH;

@RestController
@RequestMapping(PAYMENTS_PATH)
public class PaymentResource {

    private final PaymentFacade paymentFacade;

    @Autowired
    public PaymentResource(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    @GetMapping(INFO_PATH)
    public ResponseEntity<?> paymentInfo() {

        return ResponseEntity.ok("dududu");
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentDetailsRequest paymentDetailsRequest) {
        // TODO: response object
        Payment payment = paymentFacade.createNewPayment(paymentDetailsRequest);
        return ResponseEntity.ok(payment);
    }

}

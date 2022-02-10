package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.payment.converter.PaymentDetailsConverter;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentDetailsResponse;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.service.NodeDetailsService;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.INFO_PATH;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.PAYMENTS_PATH;

@RestController
@RequestMapping(PAYMENTS_PATH)
public class PaymentResource {

    private final PaymentFacade paymentFacade;
    private final NodeDetailsService nodeDetailsService;
    private final PaymentDetailsConverter paymentDetailsConverter;

    @Autowired
    public PaymentResource(PaymentFacade paymentFacade, NodeDetailsService nodeDetailsService, PaymentDetailsConverter paymentDetailsConverter) {
        this.paymentFacade = paymentFacade;
        this.nodeDetailsService = nodeDetailsService;
        this.paymentDetailsConverter = paymentDetailsConverter;
    }

    @GetMapping(INFO_PATH)
    public ResponseEntity<?> paymentInfo() {

        String nodeInfo = nodeDetailsService.getNodeUrl();
        return ResponseEntity.ok("dududu");
    }

    @PostMapping
    public ResponseEntity<PaymentDetailsResponse> createPayment(@RequestBody PaymentDetailsRequest paymentDetailsRequest) {
        Payment payment = paymentFacade.createNewPayment(paymentDetailsRequest);
        PaymentDetailsResponse response = paymentDetailsConverter.convertToDto(payment);
        return ResponseEntity.ok(response);
    }

}

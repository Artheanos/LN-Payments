package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.payment.converter.PaymentDetailsConverter;
import pl.edu.pjatk.lnpayments.webservice.payment.converter.PaymentInfoConverter;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsResponse;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentInfo;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentInfoResponse;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.INFO_PATH;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.PAYMENTS_PATH;

@RestController
@RequestMapping(PAYMENTS_PATH)
public class PaymentResource {

    private final PaymentFacade paymentFacade;
    private final PaymentDetailsConverter paymentDetailsConverter;
    private final PaymentInfoConverter paymentInfoConverter;

    @Autowired
    PaymentResource(PaymentFacade paymentFacade, PaymentDetailsConverter paymentDetailsConverter, PaymentInfoConverter paymentInfoConverter) {
        this.paymentFacade = paymentFacade;
        this.paymentDetailsConverter = paymentDetailsConverter;
        this.paymentInfoConverter = paymentInfoConverter;
    }

    @GetMapping(INFO_PATH)
    public ResponseEntity<?> paymentInfo() {
        PaymentInfo paymentInfo = paymentFacade.buildInfoResponse();
        PaymentInfoResponse paymentInfoResponse = paymentInfoConverter.convertToDto(paymentInfo);
        return ResponseEntity.ok(paymentInfoResponse);
    }

    @PostMapping
    public ResponseEntity<PaymentDetailsResponse> createPayment(@RequestBody PaymentDetailsRequest paymentDetailsRequest) {
        Payment payment = paymentFacade.createNewPayment(paymentDetailsRequest);
        PaymentDetailsResponse response = paymentDetailsConverter.convertToDto(payment);
        return ResponseEntity.ok(response);
    }

}

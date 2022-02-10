package pl.edu.pjatk.lnpayments.webservice.payment.converter;

import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsResponse;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;

@Service
public class PaymentDetailsConverter {

    public PaymentDetailsResponse convertToDto(Payment payment) {
        return PaymentDetailsResponse.builder()
                .paymentRequest(payment.getPaymentRequest())
                .timestamp(payment.getDate())
                .expirationTimestamp(payment.getExpiry())
                .build();
    }
}

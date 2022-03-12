package pl.edu.pjatk.lnpayments.webservice.payment.resource.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsResponse;

@Service
public class PaymentDetailsConverter {

    public PaymentDetailsResponse convertToDto(Payment payment) {
        return PaymentDetailsResponse.builder()
                .paymentRequest(payment.getPaymentRequest())
                .timestamp(payment.getDate())
                .expirationTimestamp(payment.getExpiry())
                .numberOfTokens(payment.getNumberOfTokens())
                .price(payment.getPrice())
                .paymentStatus(payment.getStatus())
                .tokens(payment.getTokens())
                .build();
    }

    public Page<PaymentDetailsResponse> convertPageToDto(Page<Payment> payments) {
        return payments.map(this::convertToDto);
    }
}

package pl.edu.pjatk.lnpayments.webservice.payment.resource.converter;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentAdminDetailsResponse;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentDetailsConverter {

    public PaymentDetailsResponse convertToDto(Payment payment) {
        String topic = DigestUtils.sha256Hex(payment.getPaymentRequest()).substring(0, 8);

        return PaymentDetailsResponse.builder()
                .paymentRequest(payment.getPaymentRequest())
                .paymentTopic(topic)
                .timestamp(payment.getDate())
                .expirationTimestamp(payment.getExpiry())
                .numberOfTokens(payment.getNumberOfTokens())
                .price(payment.getPrice())
                .paymentStatus(payment.getStatus())
                .tokens(extractTokenStrings(payment))
                .build();
    }

    private List<String> extractTokenStrings(Payment payment) {
        return payment.getTokens().stream().map(Token::getSequence).collect(Collectors.toList());
    }

    public PaymentAdminDetailsResponse convertToAdminDto(Payment payment) {
        return PaymentAdminDetailsResponse.builder()
                .email(payment.getUser().getEmail())
                .numberOfTokens(payment.getNumberOfTokens())
                .price(payment.getPrice())
                .timestamp(payment.getDate())
                .paymentStatus(payment.getStatus())
                .build();
    }

    public Page<PaymentDetailsResponse> convertPageToDto(Page<Payment> payments) {
        return payments.map(this::convertToDto);
    }

    public Page<PaymentAdminDetailsResponse> convertPageToAdminDto(Page<Payment> payments) {
        return payments.map(this::convertToAdminDto);
    }
}

package pl.edu.pjatk.lnpayments.webservice.payment.resource.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentInfo;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsResponse;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentInfoResponse;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class PaymentInfoConverter {

    private final PaymentDetailsConverter paymentDetailsConverter;

    @Autowired
    PaymentInfoConverter(PaymentDetailsConverter paymentDetailsConverter) {
        this.paymentDetailsConverter = paymentDetailsConverter;
    }

    public PaymentInfoResponse convertToDto(PaymentInfo paymentInfo) {
        return PaymentInfoResponse.builder()
                .price(paymentInfo.getPrice())
                .nodeUrl(paymentInfo.getNodeUrl())
                .description(paymentInfo.getDescription())
                .pendingPayments(mapPaymentToResponse(paymentInfo.getPendingPayments()))
                .build();
    }

    private Collection<PaymentDetailsResponse> mapPaymentToResponse(Collection<Payment> paymentList) {
        return paymentList.stream()
                .map(paymentDetailsConverter::convertToDto)
                .collect(Collectors.toList());
    }
}

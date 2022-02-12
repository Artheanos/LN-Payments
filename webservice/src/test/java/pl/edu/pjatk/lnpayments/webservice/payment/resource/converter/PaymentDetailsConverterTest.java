package pl.edu.pjatk.lnpayments.webservice.payment.resource.converter;

import org.junit.jupiter.api.Test;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsResponse;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentDetailsConverterTest {

    private final PaymentDetailsConverter paymentDetailsConverter = new PaymentDetailsConverter();

    @Test
    void shouldConvertToDto() {
        Payment payment = new Payment("123", 123, PaymentStatus.PENDING);

        PaymentDetailsResponse response = paymentDetailsConverter.convertToDto(payment);

        assertThat(response.getPaymentRequest()).isEqualTo("123");
        assertThat(response.getTimestamp()).isBefore(response.getExpirationTimestamp());
    }
}

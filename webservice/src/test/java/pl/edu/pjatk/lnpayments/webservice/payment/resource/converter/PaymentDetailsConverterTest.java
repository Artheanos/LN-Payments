package pl.edu.pjatk.lnpayments.webservice.payment.resource.converter;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentDetailsConverterTest {

    private final PaymentDetailsConverter paymentDetailsConverter = new PaymentDetailsConverter();

    @Test
    void shouldConvertToDto() {
        Payment payment = new Payment("123", 1, 1, 123, PaymentStatus.PENDING, null);

        PaymentDetailsResponse response = paymentDetailsConverter.convertToDto(payment);

        assertThat(response.getPaymentRequest()).isEqualTo("123");
        assertThat(response.getNumberOfTokens()).isEqualTo(1);
        assertThat(response.getPrice()).isEqualTo(1);
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getTokens()).isEmpty();
        assertThat(response.getTimestamp()).isBefore(response.getExpirationTimestamp());
    }

    @Test
    void shouldConvertPageToDto() {
        Page<Payment> payments = new PageImpl<>(List.of(
                new Payment("123", 1, 1, 123, PaymentStatus.PENDING, null),
                new Payment("456", 3, 2, 126, PaymentStatus.COMPLETE, null),
                new Payment("789", 4, 3, 129, PaymentStatus.CANCELLED, null)
        ));

        Page<PaymentDetailsResponse> result = paymentDetailsConverter.convertPageToDto(payments);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(PaymentDetailsResponse::getPaymentRequest)
                .containsExactlyInAnyOrder("123", "456", "789");
    }

}

package pl.edu.pjatk.lnpayments.webservice.payment.resource.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentInfo;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentInfoResponse;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PaymentInfoConverterTest {

    @Mock
    private PaymentDetailsConverter paymentDetailsConverter;

    @InjectMocks
    private PaymentInfoConverter paymentInfoConverter;

    @Test
    void shouldProperlyConvertToDto() {
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .price(1)
                .nodeUrl("test1")
                .description("test2")
                .pendingPayments(Collections.emptyList())
                .build();

        PaymentInfoResponse response = paymentInfoConverter.convertToDto(paymentInfo);

        assertThat(response.getNodeUrl()).isEqualTo("test1");
        assertThat(response.getDescription()).isEqualTo("test2");
        assertThat(response.getPrice()).isEqualTo(1);
        assertThat(response.getPendingPayments()).isEmpty();
    }

}

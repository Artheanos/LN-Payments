package pl.edu.pjatk.lnpayments.webservice.payment.resource;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.converter.PaymentDetailsConverter;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.converter.PaymentInfoConverter;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PaymentResourceTest {

    @Mock
    private PaymentFacade paymentFacade;

    @Mock
    private PaymentDetailsConverter paymentDetailsConverter;

    @Mock
    private PaymentInfoConverter paymentInfoConverter;

    @InjectMocks
    private PaymentResource paymentResource;

    @Nested
    class PaymentDetailsTest {

        @Test
        void shouldReturnStatusOkWhenProperDataPassed() {
            PaymentDetailsRequest request = new PaymentDetailsRequest(1);

            ResponseEntity<?> payment = paymentResource.createPayment(request, () -> null);

            assertThat(payment.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

    }

    @Nested
    class PaymentInfoTest {

        @Test
        void shouldReturnOkForAnyRequest() {
            ResponseEntity<?> payment = paymentResource.paymentInfo(() -> "test");

            assertThat(payment.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void shouldReturnOkWhenPrincipalIsNull() {
            ResponseEntity<?> payment = paymentResource.paymentInfo(null);

            assertThat(payment.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class UserPaymentsTest {

        @Test
        void shouldReturnOkForValidRequest() {
            ResponseEntity<?> payments = paymentResource.getAllUserPayments(() -> "test", null);

            assertThat(payments.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class AllPaymentsTest {
        @Test
        void shouldReturnOkForValidRequest() {
            ResponseEntity<?> payments = paymentResource.getAllPayments(null);

            assertThat(payments.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }
}

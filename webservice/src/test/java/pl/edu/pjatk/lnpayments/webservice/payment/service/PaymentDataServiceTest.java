package pl.edu.pjatk.lnpayments.webservice.payment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.PaymentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests here are a little dump at the moment. This will probably change in the future as we add more functionalities
 * there. We must keep them for now for sake of code coverage.
 */
@ExtendWith(MockitoExtension.class)
class PaymentDataServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentDataService paymentDataService;

    @Test
    void shouldSaveItemAndReturnCopy() {
        Payment payment = new Payment("asd", 60, PaymentStatus.PENDING);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment returnedPayment = paymentDataService.savePayment(payment);

        assertThat(returnedPayment).isEqualTo(payment);
    }

    @Test
    void shouldReturnEmptyListOfPendingTransactionWhenThereAreNone() {
        //TODO: implement in LP-xxx
        List<Payment> payments = paymentDataService.findPendingPaymentsByUser();
        assertThat(payments).isEmpty();
    }
}

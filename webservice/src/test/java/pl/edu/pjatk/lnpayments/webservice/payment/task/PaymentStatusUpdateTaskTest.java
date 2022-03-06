package pl.edu.pjatk.lnpayments.webservice.payment.task;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.service.PaymentDataService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PaymentStatusUpdateTaskTest {

    @Test
    void shouldChangePaymentStatusItTimedOut() {
        PaymentDataService paymentDataService = Mockito.mock(PaymentDataService.class);
        Payment payment = Payment.builder()
                .paymentStatus(PaymentStatus.PENDING)
                .expiryInSeconds(0)
                .build();
        PaymentStatusUpdateTask paymentStatusUpdateTask = new PaymentStatusUpdateTask(payment, paymentDataService);
        paymentStatusUpdateTask.run();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        verify(paymentDataService).savePayment(payment);
    }

    @Test
    void shouldNotChangeStatusWhenAlreadyCompleted() {
        PaymentDataService paymentDataService = Mockito.mock(PaymentDataService.class);
        Payment payment = Payment.builder()
                .paymentStatus(PaymentStatus.COMPLETE)
                .expiryInSeconds(0)
                .build();
        PaymentStatusUpdateTask paymentStatusUpdateTask = new PaymentStatusUpdateTask(payment, paymentDataService);
        paymentStatusUpdateTask.run();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
        verify(paymentDataService, never()).savePayment(payment);
    }

}
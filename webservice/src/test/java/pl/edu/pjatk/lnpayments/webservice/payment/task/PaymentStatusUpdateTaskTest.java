package pl.edu.pjatk.lnpayments.webservice.payment.task;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.service.PaymentDataService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentStatusUpdateTaskTest {

    @Test
    void shouldChangePaymentStatusItTimedOut() {
        PaymentDataService paymentDataService = Mockito.mock(PaymentDataService.class);
        Payment payment = Payment.builder()
                .paymentRequest("aaa")
                .paymentStatus(PaymentStatus.PENDING)
                .expiryInSeconds(0)
                .build();
        when(paymentDataService.findPaymentByRequest(any())).thenReturn(payment);
        PaymentStatusUpdateTask paymentStatusUpdateTask = new PaymentStatusUpdateTask(payment, paymentDataService);
        paymentStatusUpdateTask.run();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        verify(paymentDataService).savePayment(payment);
        verify(paymentDataService).findPaymentByRequest(any());
    }

    @Test
    void shouldNotChangeStatusWhenAlreadyCompleted() {
        PaymentDataService paymentDataService = Mockito.mock(PaymentDataService.class);
        Payment payment = Payment.builder()
                .paymentStatus(PaymentStatus.COMPLETE)
                .expiryInSeconds(0)
                .build();
        when(paymentDataService.findPaymentByRequest(any())).thenReturn(payment);
        PaymentStatusUpdateTask paymentStatusUpdateTask = new PaymentStatusUpdateTask(payment, paymentDataService);
        paymentStatusUpdateTask.run();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
        verify(paymentDataService, never()).savePayment(payment);
        verify(paymentDataService).findPaymentByRequest(any());
    }

}
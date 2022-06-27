package pl.edu.pjatk.lnpayments.webservice.payment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.PaymentRepository;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.enums.SearchableField;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.specification.PaymentSpecification;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentDataServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentDataService paymentDataService;

    @Test
    void shouldSaveItemAndReturnCopy() {
        Payment payment = new Payment("asd", 1, 1, 60, PaymentStatus.PENDING, null);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment returnedPayment = paymentDataService.savePayment(payment);

        assertThat(returnedPayment).isEqualTo(payment);
    }

    @Test
    void shouldReturnEmptyListOfPendingTransactionWhenThereAreNone() {
        Collection<Payment> payments = paymentDataService.findPendingPaymentsByUser(null);
        assertThat(payments).isEmpty();
    }

    @Test
    void shouldFindPaymentById() {
        String paymentRequest = "asd";
        Payment payment = new Payment(paymentRequest, 1, 1, 60, PaymentStatus.PENDING, null);
        when(paymentRepository.findPaymentByPaymentRequest(paymentRequest)).thenReturn(Optional.of(payment));

        Payment result = paymentDataService.findPaymentByRequest(paymentRequest);

        assertThat(payment).isEqualTo(result);
    }

    @Test
    void shouldThrowExceptionWhenNoEntityFound() {
        when(paymentRepository.findPaymentByPaymentRequest(anyString())).thenReturn(Optional.empty());

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> paymentDataService.findPaymentByRequest(""));
    }

    @Test
    void shouldReturnPageUsingQuery() {
        List<Payment> payments = List.of(
                new Payment("123", 1, 1, 123, PaymentStatus.PENDING, null),
                new Payment("456", 3, 2, 126, PaymentStatus.COMPLETE, null),
                new Payment("789", 4, 3, 129, PaymentStatus.CANCELLED, null)
        );
        when(paymentRepository.findAll(any(PaymentSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(payments));

        Page<Payment> response = paymentDataService.findAll(SearchableField.EMAIL, "test", PageRequest.ofSize(3));

        assertThat(response).hasSize(3);
    }

    @Test
    void shouldReturnEmptyListWhenNoPayments() {
        when(paymentRepository.findAll(any(PaymentSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<Payment> response = paymentDataService.findAll(SearchableField.EMAIL, "test", PageRequest.ofSize(3));

        assertThat(response).isEmpty();
    }

    @Test
    void shouldReturnAllPaymentsInPage() {
        List<Payment> payments = List.of(
                new Payment("123", 1, 1, 123, PaymentStatus.PENDING, null),
                new Payment("456", 3, 2, 126, PaymentStatus.COMPLETE, null),
                new Payment("789", 4, 3, 129, PaymentStatus.CANCELLED, null)
        );
        when(paymentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(payments));

        Page<Payment> response = paymentDataService.findAll(PageRequest.ofSize(3));

        assertThat(response).hasSize(3);
    }

    @Test
    void shouldReturnEmptyArrayWhenNoPayments() {
        when(paymentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        Page<Payment> response = paymentDataService.findAll(PageRequest.ofSize(3));

        assertThat(response).isEmpty();
    }

    @Test
    void shouldReturnAllPayments() {
        List<Payment> payments = List.of(
                new Payment("123", 1, 1, 123, PaymentStatus.PENDING, null),
                new Payment("456", 3, 2, 126, PaymentStatus.COMPLETE, null),
                new Payment("789", 4, 3, 129, PaymentStatus.CANCELLED, null)
        );
        when(paymentRepository.findAll()).thenReturn(payments);

        Collection<Payment> response = paymentDataService.findAll();

        assertThat(response).hasSize(3);
    }
}

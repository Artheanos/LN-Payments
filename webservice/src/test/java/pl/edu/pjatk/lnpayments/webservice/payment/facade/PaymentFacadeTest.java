package pl.edu.pjatk.lnpayments.webservice.payment.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.TaskScheduler;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.AggregatedData;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentInfo;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.enums.SearchableField;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.PaymentSocketController;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.service.*;
import pl.edu.pjatk.lnpayments.webservice.payment.task.PaymentStatusUpdateTask;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory.createUser;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private PropertyService propertyService;

    @Mock
    private PaymentDataService paymentDataService;

    @Mock
    private NodeDetailsService nodeDetailsService;

    @Mock
    private TokenDeliveryService tokenDeliveryService;

    @Mock
    private PaymentSocketController paymentSocketController;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @Mock
    private TaskScheduler taskScheduler;

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Nested
    class BuildInfoResponse {
        @Test
        void shouldBuildPaymentInfo() {
            String email = "test@test.pl";
            when(paymentDataService.findPendingPaymentsByUser(email)).thenReturn(Collections.emptyList());
            when(nodeDetailsService.getNodeUrl()).thenReturn("node_url");
            when(propertyService.getPrice()).thenReturn(1);
            when(propertyService.getDescription()).thenReturn("description");

            PaymentInfo paymentInfo = paymentFacade.buildInfoResponse(Optional.of(email));

            assertThat(paymentInfo.getPendingPayments()).isEmpty();
            assertThat(paymentInfo.getPrice()).isEqualTo(1);
            assertThat(paymentInfo.getNodeUrl()).isEqualTo("node_url");
            assertThat(paymentInfo.getDescription()).isEqualTo("description");
        }

        @Test
        void shouldReturnInfoWhenNoEmailProvided() {
            when(nodeDetailsService.getNodeUrl()).thenReturn("node_url");
            when(propertyService.getPrice()).thenReturn(1);
            when(propertyService.getDescription()).thenReturn("description");

            PaymentInfo paymentInfo = paymentFacade.buildInfoResponse(Optional.empty());

            assertThat(paymentInfo.getPendingPayments()).isEmpty();
            assertThat(paymentInfo.getPrice()).isEqualTo(1);
            assertThat(paymentInfo.getNodeUrl()).isEqualTo("node_url");
            assertThat(paymentInfo.getDescription()).isEqualTo("description");
        }
    }

    @Nested
    class CreateNewPayment {
        @Test
        void shouldCreatePaymentForValidRequest() {
            PaymentDetailsRequest request = new PaymentDetailsRequest(1);
            when(propertyService.getPrice()).thenReturn(1);
            when(propertyService.getInvoiceMemo()).thenReturn("memo");
            when(propertyService.getPaymentExpiryInSeconds()).thenReturn(100);
            when(paymentDataService.savePayment(any(Payment.class))).then(AdditionalAnswers.returnsFirstArg());
            when(invoiceService.createInvoice(anyInt(), anyInt(), anyInt(), any())).thenReturn("invoice");

            Payment response = paymentFacade.createNewPayment(request, "test");

            assertThat(response.getPaymentRequest()).isEqualTo("invoice");
            assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(response.getDate()).isBefore(response.getExpiry());
            ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
            verify(paymentDataService).savePayment(paymentCaptor.capture());
            verify(taskScheduler).schedule(any(PaymentStatusUpdateTask.class), eq(paymentCaptor.getValue().getExpiry()));
        }
    }

    @Nested
    class FinalizePayment {
        @Test
        void shouldFinalizePayment() {
            Payment payment = new Payment("asd", 1, 1, 60, PaymentStatus.PENDING, createUser(""));
            when(paymentDataService.findPaymentByRequest("asd")).thenReturn(payment);
            when(tokenService.generateTokens(payment)).thenReturn(Collections.singleton(new Token("aaa")));

            paymentFacade.finalizePayment("asd");

            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
            assertThat(payment.getTokens()).isNotEmpty();
            verify(tokenService).generateTokens(payment);
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(paymentSocketController).sendTokens(captor.capture(), anyCollection());
            verify(tokenDeliveryService).send(Collections.singleton(new Token("aaa")));
            assertThat(captor.getValue()).hasSize(8);
        }
    }

    @Nested
    class GetPaymentsByEmail {
        @Test
        void shouldReturnPaymentsForEmail() {
            Page<Payment> payments = new PageImpl<>(List.of(
                    new Payment("123", 1, 1, 123, PaymentStatus.PENDING, null),
                    new Payment("456", 3, 2, 126, PaymentStatus.COMPLETE, null),
                    new Payment("789", 4, 3, 129, PaymentStatus.CANCELLED, null)
            ));
            when(paymentDataService.findAll(SearchableField.EMAIL, "email", null)).thenReturn(payments);

            Page<Payment> response = paymentFacade.getPaymentsByEmail("email", null);
            assertThat(response).hasSize(3);
            verify(paymentDataService).findAll(any(), anyString(), any());
        }
    }

    @Nested
    class GetAllPayments {
        @Test
        void shouldReturnAllPayments() {
            Page<Payment> payments = new PageImpl<>(List.of(
                    new Payment("123", 1, 1, 123, PaymentStatus.PENDING, null),
                    new Payment("456", 3, 2, 126, PaymentStatus.COMPLETE, null),
                    new Payment("789", 4, 3, 129, PaymentStatus.CANCELLED, null)
            ));
            when(paymentDataService.findAll(null)).thenReturn(payments);

            Page<Payment> response = paymentFacade.getAllPayments(null);
            assertThat(response).hasSize(3);
            verify(paymentDataService).findAll(any());
        }
    }

    @Nested
    class AggregateTotalIncomeData {
        @Test
        void shouldAggregateHistoricPaymentData() {
            Instant date = Instant.ofEpochSecond(1656272934);
            Payment payment1 = new Payment("123", 1, 1, 123, PaymentStatus.COMPLETE, null);
            payment1.setDate(date);
            Payment payment2 = new Payment("125", 1, 2, 123, PaymentStatus.COMPLETE, null);
            payment2.setDate(date.minusSeconds(3_000_000));
            Payment payment3 = new Payment("124", 1, 3, 123, PaymentStatus.COMPLETE, null);
            payment3.setDate(date.minusSeconds(6_000_000));
            when(paymentDataService.findAll()).thenReturn(List.of(payment1, payment2, payment3));

            List<AggregatedData> result = paymentFacade.aggregateTotalIncomeData();

            assertThat(result.size()).isEqualTo(3);
            assertThat(result.get(0).value()).isEqualTo(3);
            assertThat(result.get(1).value()).isEqualTo(2);
            assertThat(result.get(2).value()).isEqualTo(1);
        }
    }
}

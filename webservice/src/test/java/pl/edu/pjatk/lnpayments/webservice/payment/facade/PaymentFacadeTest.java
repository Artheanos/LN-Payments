package pl.edu.pjatk.lnpayments.webservice.payment.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentInfo;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Token;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.PaymentSocketController;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.service.InvoiceService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.NodeDetailsService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.PaymentDataService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.TokenService;

import java.util.Collections;
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
    private PaymentSocketController paymentSocketController;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentFacade paymentFacade;

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
        verify(paymentDataService).savePayment(any());
    }

    @Test
    void shouldShouldFinalizePayment() {
        Payment payment = new Payment("asd", 1, 1, 60, PaymentStatus.PENDING, createUser(""));
        when(paymentDataService.findPaymentByRequest("asd")).thenReturn(payment);
        when(tokenService.generateTokens(payment)).thenReturn(Collections.singleton(new Token("aaa")));

        paymentFacade.finalizePayment("asd");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
        assertThat(payment.getTokens()).isNotEmpty();
        verify(tokenService).generateTokens(payment);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(paymentSocketController).sendTokens(captor.capture(), anyCollection());
        assertThat(captor.getValue()).hasSize(8);
    }

}

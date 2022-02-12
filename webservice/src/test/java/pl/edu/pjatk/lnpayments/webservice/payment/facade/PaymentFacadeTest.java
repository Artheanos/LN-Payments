package pl.edu.pjatk.lnpayments.webservice.payment.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentInfo;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.service.InvoiceService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.NodeDetailsService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.PaymentDataService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private PaymentFacade paymentFacade;

    @BeforeEach
    void setUp() {
        when(propertyService.getPrice()).thenReturn(1);
    }

    @Test
    void shouldBuildPaymentInfo() {
        when(paymentDataService.findPendingPaymentsByUser()).thenReturn(Collections.emptyList());
        when(nodeDetailsService.getNodeUrl()).thenReturn("node_url");
        when(propertyService.getDescription()).thenReturn("description");

        PaymentInfo paymentInfo = paymentFacade.buildInfoResponse();

        assertThat(paymentInfo.getPendingPayments()).isEmpty();
        assertThat(paymentInfo.getPrice()).isEqualTo(1);
        assertThat(paymentInfo.getNodeUrl()).isEqualTo("node_url");
        assertThat(paymentInfo.getDescription()).isEqualTo("description");
    }

    @Test
    void shouldCreatePaymentForValidRequest() {
        PaymentDetailsRequest request = new PaymentDetailsRequest(1, "email");
        when(propertyService.getInvoiceMemo()).thenReturn("memo");
        when(propertyService.getPaymentExpiryInSeconds()).thenReturn(100);
        when(paymentDataService.savePayment(any(Payment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(invoiceService.createInvoice(anyInt(), anyInt(), anyInt(), any())).thenReturn("invoice");

        Payment response = paymentFacade.createNewPayment(request);

        assertThat(response.getPaymentRequest()).isEqualTo("invoice");
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getDate()).isBefore(response.getExpiry());
    }
}

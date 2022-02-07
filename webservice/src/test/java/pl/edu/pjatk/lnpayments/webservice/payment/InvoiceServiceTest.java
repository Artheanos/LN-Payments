package pl.edu.pjatk.lnpayments.webservice.payment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.criteria.CriteriaBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private SynchronousLndAPI synchronousLndAPI;

    @InjectMocks
    private InvoiceService invoiceService;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        when(synchronousLndAPI.addInvoice(any())).thenReturn(getAddInvoiceResponse());
    }

    @Test
    @SneakyThrows
    void shouldGenerateInvoiceForCorrectInput() {
        String expected = getAddInvoiceResponse().getPaymentRequest();

        String result = invoiceService.createInvoice(1, 1, 1, "test");

        assertThat(result).isEqualTo(expected);
    }

    private AddInvoiceResponse getAddInvoiceResponse() {
        AddInvoiceResponse addInvoiceResponse = new AddInvoiceResponse();
        addInvoiceResponse.setAddIndex(0);
        addInvoiceResponse.setPaymentRequest("test");
        return addInvoiceResponse;
    }
}
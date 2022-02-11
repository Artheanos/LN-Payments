package pl.edu.pjatk.lnpayments.webservice.payment.helper.config;

import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse;
import org.lightningj.lnd.wrapper.message.GetInfoResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    SynchronousLndAPI synchronousLndAPI() throws StatusException, ValidationException {
        SynchronousLndAPI api = Mockito.mock(SynchronousLndAPI.class);
        GetInfoResponse getInfoResponse = new GetInfoResponse();
        getInfoResponse.setIdentityPubkey("key");
        when(api.getInfo()).thenReturn(getInfoResponse);
        AddInvoiceResponse invoice = new AddInvoiceResponse();
        invoice.setPaymentRequest("invoice");
        when(api.addInvoice(any())).thenReturn(invoice);
        return api;
    }
}

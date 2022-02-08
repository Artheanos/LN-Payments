package pl.edu.pjatk.lnpayments.webservice.payment;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.SneakyThrows;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    private ListAppender<ILoggingEvent> logAppender;

    @Mock
    private SynchronousLndAPI synchronousLndAPI;

    @InjectMocks
    private InvoiceService invoiceService;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(InvoiceService.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @Test
    @SneakyThrows
    void shouldGenerateInvoiceForCorrectInput() {
        when(synchronousLndAPI.addInvoice(any())).thenReturn(getAddInvoiceResponse());
        String expected = getAddInvoiceResponse().getPaymentRequest();

        String result = invoiceService.createInvoice(1, 1, 1, "test");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    void shouldThrowLightningExceptionWhenLndReturnsError() {
        when(synchronousLndAPI.addInvoice(any())).thenThrow(ValidationException.class);

        assertThatExceptionOfType(LightningException.class)
                .isThrownBy(() -> invoiceService.createInvoice(1, 1, 1, "test"))
                .withMessageStartingWith("Error creating invoice")
                .withCauseExactlyInstanceOf(ValidationException.class);
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("LND has thrown exception while creating invoice: ", Level.ERROR));
    }

    private AddInvoiceResponse getAddInvoiceResponse() {
        AddInvoiceResponse addInvoiceResponse = new AddInvoiceResponse();
        addInvoiceResponse.setAddIndex(0);
        addInvoiceResponse.setPaymentRequest("test");
        return addInvoiceResponse;
    }
}
package pl.edu.pjatk.lnpayments.webservice.payment.observer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvoiceObserverTest {

    private ListAppender<ILoggingEvent> logAppender;

    @Mock
    private PaymentFacade paymentFacade;

    @InjectMocks
    private InvoiceObserver invoiceObserver;

    @BeforeEach
    void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(InvoiceObserver.class)).addAppender(logAppender);
    }

    @Test
    void shouldLogMessageOnCompleted() {
        invoiceObserver.onCompleted();
        assertThat(logAppender.list)
            .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
            .contains(Tuple.tuple("Invoice listener closed", Level.INFO));
    }

    @Test
    void shouldLogAndThrowExceptionOnError() {
        Throwable throwable = new RuntimeException();
        assertThatExceptionOfType(LightningException.class)
                .isThrownBy(() -> invoiceObserver.onError(throwable))
                .withMessage("Something went wrong with invoice subscriber");
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Invoice observer has thrown an error", Level.ERROR));
    }

    @Test
    void shouldCallPaymentFacadeWhenInvoiceIsSettled() {
        Invoice invoice = new Invoice();
        invoice.setPaymentRequest("asd");
        invoice.setState(Invoice.InvoiceState.SETTLED);

        invoiceObserver.onNext(invoice);

        verify(paymentFacade).finalizePayment(any());
    }

    @Test
    void shouldDoNothingWhenInvoiceNotSettled() {
        Invoice invoice = new Invoice();
        invoice.setState(Invoice.InvoiceState.OPEN);

        invoiceObserver.onNext(invoice);

        verify(paymentFacade, never()).finalizePayment(any());
    }

}

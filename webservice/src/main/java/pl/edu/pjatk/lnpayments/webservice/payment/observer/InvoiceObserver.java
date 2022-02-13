package pl.edu.pjatk.lnpayments.webservice.payment.observer;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.wrapper.message.Invoice;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;

@Slf4j
public class InvoiceObserver implements StreamObserver<Invoice> {

    private final PaymentFacade paymentFacade;

    public InvoiceObserver(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    @Override
    public void onNext(Invoice invoice) {
        if (invoice.getState() == Invoice.InvoiceState.SETTLED) {
            paymentFacade.finalizePayment(invoice.getPaymentRequest());
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error("Invoice observer has thrown an error");
        throw new LightningException("Something went wrong with invoice subscriber", t);
    }

    @Override
    public void onCompleted() {
        log.info("Invoice listener closed");
    }

}

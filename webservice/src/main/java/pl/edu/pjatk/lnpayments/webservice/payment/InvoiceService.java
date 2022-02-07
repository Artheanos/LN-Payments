package pl.edu.pjatk.lnpayments.webservice.payment;

import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    private final SynchronousLndAPI synchronousLndAPI;

    @Autowired
    public InvoiceService(SynchronousLndAPI synchronousLndAPI) {
        this.synchronousLndAPI = synchronousLndAPI;
    }

    /**
     * Generates invoice and sends it to LND.
     *
     * @param tokenAmount Amount of tokens to buy
     * @param price Price of single token
     * @param expiry Time in seconds in which invoice will expire
     * @param memo Message that is displayed together with invoice
     * @return payment request string, that user can use to pay the invoice
     */
    //TODO: Discuss exception handling
    public String createInvoice(int tokenAmount, int price, int expiry, String memo)
            throws StatusException, ValidationException {
        int invoiceValue = tokenAmount * price;
        Invoice invoice = generateInvoice(expiry, memo, invoiceValue);
        AddInvoiceResponse addInvoiceResponse = synchronousLndAPI.addInvoice(invoice);
        return addInvoiceResponse.getPaymentRequest();
    }

    private Invoice generateInvoice(int expiry, String memo, int invoiceValue) {
        Invoice invoice = new Invoice();
        invoice.setValue(invoiceValue);
        invoice.setMemo(memo);
        invoice.setExpiry(expiry);
        return invoice;
    }

}

package pl.edu.pjatk.lnpayments.webservice.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

@Slf4j
@Service
public class InvoiceService {

    private final SynchronousLndAPI synchronousLndAPI;

    @Autowired
    InvoiceService(SynchronousLndAPI synchronousLndAPI) {
        this.synchronousLndAPI = synchronousLndAPI;
    }

    /**
     * Generates invoice and sends it to LND.
     *
     * @param tokenAmount Amount of tokens to buy
     * @param price Price of single token
     * @param expiryInSeconds Time in seconds in which invoice will expire
     * @param memo Message that is displayed together with invoice
     * @return payment request string, that user can use to pay the invoice
     */
    public String createInvoice(int tokenAmount, int price, int expiryInSeconds, String memo) {
        int invoiceValue = tokenAmount * price;
        Invoice invoice = generateInvoice(expiryInSeconds, memo, invoiceValue);
        try {
            AddInvoiceResponse addInvoiceResponse = synchronousLndAPI.addInvoice(invoice);
            return addInvoiceResponse.getPaymentRequest();
        } catch (StatusException | ValidationException e) {
            log.error("LND has thrown exception while creating invoice: ", e);
            throw new LightningException("Error creating invoice", e);
        }
    }

    private Invoice generateInvoice(int expiryInSeconds, String memo, int invoiceValue) {
        Invoice invoice = new Invoice();
        invoice.setValue(invoiceValue);
        invoice.setMemo(memo);
        invoice.setExpiry(expiryInSeconds);
        return invoice;
    }

}

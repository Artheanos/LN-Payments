package pl.edu.pjatk.lnpayments.webservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;
import pl.edu.pjatk.lnpayments.webservice.service.InvoiceService;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final SynchronousLndAPI synchronousLndAPI;

    @Autowired
    public InvoiceServiceImpl(SynchronousLndAPI synchronousLndAPI) {
        this.synchronousLndAPI = synchronousLndAPI;
    }

    @Override
    public String createInvoice(int tokenAmount, int price, int expiry, String memo) {
        int invoiceValue = tokenAmount * price;
        Invoice invoice = generateInvoice(expiry, memo, invoiceValue);
        try {
            AddInvoiceResponse addInvoiceResponse = synchronousLndAPI.addInvoice(invoice);
            return addInvoiceResponse.getPaymentRequest();
        } catch (StatusException | ValidationException e) {
            log.error("LND has thrown exception while creating invoice: ", e);
            throw new LightningException("Error creating invoice", e);
        }
    }

    private Invoice generateInvoice(int expiry, String memo, int invoiceValue) {
        Invoice invoice = new Invoice();
        invoice.setValue(invoiceValue);
        invoice.setMemo(memo);
        invoice.setExpiry(expiry);
        return invoice;
    }

}

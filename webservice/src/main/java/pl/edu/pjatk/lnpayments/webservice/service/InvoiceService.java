package pl.edu.pjatk.lnpayments.webservice.service;

public interface InvoiceService {

    /**
     * Generates invoice and sends it to LND.
     *
     * @param tokenAmount Amount of tokens to buy
     * @param price Price of single token
     * @param expiry Time in seconds in which invoice will expire
     * @param memo Message that is displayed together with invoice
     * @return payment request string, that user can use to pay the invoice
     */
    String createInvoice(int tokenAmount, int price, int expiry, String memo);

}

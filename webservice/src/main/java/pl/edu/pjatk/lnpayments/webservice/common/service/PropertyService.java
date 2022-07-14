package pl.edu.pjatk.lnpayments.webservice.common.service;

public interface PropertyService {

    int getPrice();

    String getDescription();

    String getInvoiceMemo();

    int getPaymentExpiryInSeconds();

    long getAutoChannelCloseLimit();

    long getAutoTransferLimit();

    String getTokenDeliveryUrl();

    String getServerIpAddress();
}

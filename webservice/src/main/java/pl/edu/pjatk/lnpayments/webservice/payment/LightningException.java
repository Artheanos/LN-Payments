package pl.edu.pjatk.lnpayments.webservice.payment;

public class LightningException extends RuntimeException {

    public LightningException(String message, Throwable cause) {
        super(message, cause);
    }
}

package pl.edu.pjatk.lnpayments.webservice.payment.exception;

public class LightningException extends RuntimeException {

    public LightningException(String message, Throwable cause) {
        super(message, cause);
    }
}

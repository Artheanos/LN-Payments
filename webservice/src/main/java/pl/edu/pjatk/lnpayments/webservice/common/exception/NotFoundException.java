package pl.edu.pjatk.lnpayments.webservice.common.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

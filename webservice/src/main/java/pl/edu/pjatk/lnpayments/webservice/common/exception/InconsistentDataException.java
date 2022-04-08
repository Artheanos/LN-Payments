package pl.edu.pjatk.lnpayments.webservice.common.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InconsistentDataException extends RuntimeException {

    public InconsistentDataException(String message) {
        super(message);
    }
}

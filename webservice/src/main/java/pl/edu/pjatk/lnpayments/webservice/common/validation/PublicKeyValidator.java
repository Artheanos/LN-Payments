package pl.edu.pjatk.lnpayments.webservice.common.validation;

import org.bitcoinj.core.ECKey;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HexFormat;

public class PublicKeyValidator implements ConstraintValidator<PublicKey, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return ECKey.isPubKeyCanonical(HexFormat.of().parseHex(value));
    }
}

package pl.edu.pjatk.lnpayments.webservice.common.validation;

import org.bitcoinj.core.Address;
import org.bitcoinj.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BitcoinAddressValidator implements ConstraintValidator<BitcoinAddress, String> {

    private final WalletAppKit walletAppKit;

    @Autowired
    public BitcoinAddressValidator(WalletAppKit walletAppKit) {
        this.walletAppKit = walletAppKit;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            Address.fromString(walletAppKit.params(), value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}


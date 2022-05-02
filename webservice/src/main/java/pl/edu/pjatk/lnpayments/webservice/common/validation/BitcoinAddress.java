package pl.edu.pjatk.lnpayments.webservice.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BitcoinAddressValidator.class)
public @interface BitcoinAddress {
    String message() default "String is not an address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
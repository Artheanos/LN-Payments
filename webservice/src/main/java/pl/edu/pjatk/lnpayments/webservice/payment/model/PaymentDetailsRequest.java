package pl.edu.pjatk.lnpayments.webservice.payment.model;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@Data
@Valid
public class PaymentDetailsRequest {

    @Min(1)
    private int numberOfTokens;
    @Nullable
    private String email;

}

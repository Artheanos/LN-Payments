package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.Getter;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@Valid
@Getter
public class PaymentDetailsRequest {

    @Min(1)
    private int numberOfTokens;
    @Nullable
    private String email;

}

package pl.edu.pjatk.lnpayments.webservice.payment.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Valid
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsRequest {

    @Min(1)
    private int numberOfTokens;

}

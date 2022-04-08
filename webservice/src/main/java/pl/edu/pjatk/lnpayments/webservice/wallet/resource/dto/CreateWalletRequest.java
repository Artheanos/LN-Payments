package pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequest {

    @Min(1)
    @Max(10)
    private int minSignatures;
    @Size(min = 1)
    private List<@Email String> adminEmails;
}

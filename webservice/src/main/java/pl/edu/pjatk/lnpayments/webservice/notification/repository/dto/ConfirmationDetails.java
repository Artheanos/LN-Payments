package pl.edu.pjatk.lnpayments.webservice.notification.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationDetails {

    @NotBlank
    private String rawTransaction;

    @Min(0)
    private long version;

    @Nullable
    private String redeemScript;

}

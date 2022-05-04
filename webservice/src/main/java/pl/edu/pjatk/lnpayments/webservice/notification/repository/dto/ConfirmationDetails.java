package pl.edu.pjatk.lnpayments.webservice.notification.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationDetails {

    private String rawTransaction;
    private long version;

}

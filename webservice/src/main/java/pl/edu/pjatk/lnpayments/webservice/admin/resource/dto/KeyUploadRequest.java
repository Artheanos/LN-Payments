package pl.edu.pjatk.lnpayments.webservice.admin.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.common.validation.PublicKey;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeyUploadRequest {

    @PublicKey
    private String publicKey;

}

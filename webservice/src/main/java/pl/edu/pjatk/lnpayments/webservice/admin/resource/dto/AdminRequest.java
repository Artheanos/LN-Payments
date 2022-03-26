package pl.edu.pjatk.lnpayments.webservice.admin.resource.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;

@NoArgsConstructor
public class AdminRequest extends RegisterRequest {

    @Builder
    public AdminRequest(String email, String fullName, String password) {
        super(email, fullName, password);
    }
}

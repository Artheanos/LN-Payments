package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import lombok.Builder;

public class AdminRequest extends RegisterRequest {

    @Builder
    public AdminRequest(String email, String fullName, String password) {
        super(email, fullName, password);
    }
}

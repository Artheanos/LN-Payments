package pl.edu.pjatk.lnpayments.webservice.admin.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class AdminDeleteRequest {
    @Email
    private String email;
}

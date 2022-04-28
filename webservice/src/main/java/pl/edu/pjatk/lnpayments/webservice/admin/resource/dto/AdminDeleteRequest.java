package pl.edu.pjatk.lnpayments.webservice.admin.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDeleteRequest {
    @Email
    private String email;
}

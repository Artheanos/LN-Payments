package pl.edu.pjatk.lnpayments.webservice.admin.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AdminResponse {

    private String fullName;
    private String email;

}

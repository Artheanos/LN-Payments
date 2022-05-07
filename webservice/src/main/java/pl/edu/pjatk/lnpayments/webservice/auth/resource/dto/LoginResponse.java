package pl.edu.pjatk.lnpayments.webservice.auth.resource.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String email;
    private String fullName;
    private Role role;
    private String token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String notificationChannelId;

}

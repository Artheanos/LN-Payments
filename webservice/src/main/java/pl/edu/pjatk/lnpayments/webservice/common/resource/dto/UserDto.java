package pl.edu.pjatk.lnpayments.webservice.common.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String email;
    private String fullName;
    private Role role;
    private Instant createdAt;
}

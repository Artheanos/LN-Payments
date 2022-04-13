package pl.edu.pjatk.lnpayments.webservice.admin.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;

@Service
public class AdminConverter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public AdminUser convertToEntity(AdminRequest request) {
        return AdminUser.adminBuilder()
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
    }

    public AdminResponse convertToDto(AdminUser adminUser) {
        return AdminResponse.builder()
                .fullName(adminUser.getFullName())
                .email(adminUser.getEmail())
                .build();
    }

    public Page<AdminResponse> convertAllToDto(Page<AdminUser> admins) {
        return admins.map(this::convertToDto);
    }
}
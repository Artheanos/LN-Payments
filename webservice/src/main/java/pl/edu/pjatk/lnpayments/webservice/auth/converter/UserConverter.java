package pl.edu.pjatk.lnpayments.webservice.auth.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.auth.model.UserDetailsImpl;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.UserDto;

@Service
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public StandardUser convertToEntity(RegisterRequest request) {
        return StandardUser.builder()
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
    }

    public UserDetails convertToUserDetails(User user) {
        return UserDetailsImpl.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .password(user instanceof StandardUser u ? u.getPassword() : null)
                .build();
    }

    public LoginResponse convertToLoginResponse(StandardUser user, String token) {
        return LoginResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .token(token)
                .role(user.getRole())
                .notificationChannelId(user instanceof AdminUser u ? u.notificationsChannelId() : null)
                .build();
    }

    public UserDto convertToDto(StandardUser user) {
        return UserDto.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

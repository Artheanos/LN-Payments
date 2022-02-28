package pl.edu.pjatk.lnpayments.webservice.auth.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.auth.model.UserDetailsImpl;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;

@Service
public class StandardUserConverter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StandardUserConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public StandardUser convertToEntity(RegisterRequest request) {
        return StandardUser.builder()
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
    }

    public UserDetails convertToUserDetails(StandardUser user) {
        return UserDetailsImpl.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .password(user.getPassword())
                .build();
    }

    public LoginResponse convertToLoginResponse(StandardUser user, String token) {
        return new LoginResponse(user.getEmail(), user.getFullName(), user.getRole(), token);
    }

}

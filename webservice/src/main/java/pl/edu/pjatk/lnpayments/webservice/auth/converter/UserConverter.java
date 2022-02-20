package pl.edu.pjatk.lnpayments.webservice.auth.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.auth.model.UserDetailsImpl;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;

@Service
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User convertToEntity(RegisterRequest request, Role role) {
        return User.builder()
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .email(request.getEmail())
                .build();
    }

    public UserDetails convertToUserDetails(User user) {
        return UserDetailsImpl.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .password(user.getPassword())
                .build();
    }

}

package pl.edu.pjatk.lnpayments.webservice.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.auth.converter.UserConverter;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.StandardUserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.TemporaryUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PasswordUpdateRequest;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.UserDto;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

@Service
public class UserService implements UserDetailsService {

    private final StandardUserRepository standardUserRepository;
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(StandardUserRepository standardUserRepository,
                       UserRepository userRepository,
                       UserConverter userConverter,
                       PasswordEncoder passwordEncoder) {
        this.standardUserRepository = standardUserRepository;
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(RegisterRequest request) {
        validateEmail(request.getEmail());
        User user = userConverter.convertToEntity(request);
        userRepository.save(user);
    }

    @Transactional
    public String createTemporaryUser(String email) {
        TemporaryUser temporaryUser = new TemporaryUser(email);
        User user = userRepository.save(temporaryUser);
        return user.getEmail();
    }

    public LoginResponse findAndConvertLoggedUser(String username, String jwtToken) {
        StandardUser user = getStandardUser(username);
        return userConverter.convertToLoginResponse(user, jwtToken);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByEmail(username);
        return userConverter.convertToUserDetails(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found!"));
    }

    public void updatePassword(String username, PasswordUpdateRequest passwordUpdateRequest) {
        StandardUser user = getStandardUser(username);
        if (!passwordEncoder.matches(passwordUpdateRequest.getCurrentPassword(), user.getPassword())) {
            throw new ValidationException("Wrong current password provided");
        }
        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        standardUserRepository.save(user);
    }

    public UserDto getUserDetails(String username) {
        StandardUser user = getStandardUser(username);
        return userConverter.convertToDto(user);
    }

    private StandardUser getStandardUser(String username) {
        return standardUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found!"));
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("User with mail " + email + " exists!");
        }
    }
}

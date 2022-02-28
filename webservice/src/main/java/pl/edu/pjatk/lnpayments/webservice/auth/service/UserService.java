package pl.edu.pjatk.lnpayments.webservice.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.auth.converter.StandardUserConverter;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.StandardUserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

@Service
public class UserService implements UserDetailsService {

    private final StandardUserRepository userRepository;
    private final StandardUserConverter standardUserConverter;

    @Autowired
    public UserService(StandardUserRepository userRepository, StandardUserConverter standardUserConverter) {
        this.userRepository = userRepository;
        this.standardUserConverter = standardUserConverter;
    }

    @Transactional
    public void createUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("User with mail " + request.getEmail() + " exists!");
        }

        StandardUser user = standardUserConverter.convertToEntity(request);
        userRepository.save(user);
    }

    public LoginResponse findAndConvertLoggedUser(String username, String jwtToken) {
        StandardUser user = findStandardUser(username);
        return standardUserConverter.convertToLoginResponse(user, jwtToken);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StandardUser user = findStandardUser(username);
        return standardUserConverter.convertToUserDetails(user);
    }

    private StandardUser findStandardUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found!"));
    }

}

package pl.edu.pjatk.lnpayments.webservice.common.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;

import javax.validation.ValidationException;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.ROOT_USER_EMAIL;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.ROOT_USER_PASSWORD;

@Slf4j
@Component
class InitialDataLoader implements ApplicationRunner {

    private final UserService userService;

    @Autowired
    public InitialDataLoader(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        String password = ROOT_USER_PASSWORD;
        String email = ROOT_USER_EMAIL;
        AdminRequest request = AdminRequest.builder()
                .fullName("admin")
                .password(password)
                .email(email)
                .build();
        try {
            userService.createAdmin(request);
            log.info("Initial user has been added with email {} and password {}", email, password);
        } catch (ValidationException e) {
            //TODO Refactor when initial configuration is implemented (eg. check if server was already initialized)
            log.warn("Unable to create initial admin user: " + e.getMessage());
        }
    }
}

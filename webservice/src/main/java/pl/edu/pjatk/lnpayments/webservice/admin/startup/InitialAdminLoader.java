package pl.edu.pjatk.lnpayments.webservice.admin.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;

import javax.validation.ValidationException;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.ROOT_USER_EMAIL;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.ROOT_USER_PASSWORD;

@Slf4j
@Component
@Profile("!test")
class InitialAdminLoader implements ApplicationRunner {

    private final AdminService adminService;

    @Autowired
    public InitialAdminLoader(AdminService adminService) {
        this.adminService = adminService;
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
            adminService.createAdmin(request);
            log.info("Initial user has been added with email {} and password {}", email, password);
        } catch (ValidationException e) {
            //TODO Refactor when initial configuration is implemented (eg. check if server was already initialized)
            log.warn("Unable to create initial admin user: " + e.getMessage());
        }
    }
}

package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;

import javax.validation.Valid;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.AUTH_PATH;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.REGISTER_PATH;

@RestController
@RequestMapping(AUTH_PATH)
class AuthResource {

    private final UserService userService;

    @Autowired
    public AuthResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(REGISTER_PATH)
    ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

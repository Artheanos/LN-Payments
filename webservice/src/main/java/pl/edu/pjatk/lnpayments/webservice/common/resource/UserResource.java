package pl.edu.pjatk.lnpayments.webservice.common.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PasswordUpdateRequest;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.UserDto;

import javax.validation.Valid;
import java.security.Principal;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.PASSWORD_PATH;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.USERS_PATH;

@RestController
@RequestMapping(USERS_PATH)
class UserResource {

    private final UserService userService;

    @Autowired
    UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    ResponseEntity<UserDto> getDetails(Principal principal) {
        UserDto user = userService.getUserDetails(principal.getName());
        return ResponseEntity.ok(user);
    }

    @PutMapping(PASSWORD_PATH)
    ResponseEntity<?> updatePassword(@RequestBody @Valid PasswordUpdateRequest passwordRequest, Principal principal) {
        userService.updatePassword(principal.getName(), passwordRequest);
        return ResponseEntity.ok().build();
    }
}

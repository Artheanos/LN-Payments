package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RefreshTokenResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;

import javax.validation.Valid;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.*;

@RestController
@RequestMapping(AUTH_PATH)
class AuthResource {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthResource(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping(REGISTER_PATH)
    ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(REFRESH_PATH)
    ResponseEntity<RefreshTokenResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String newToken = jwtService.refreshToken(jwtService.headerToToken(authHeader).get());
        return ResponseEntity.ok(new RefreshTokenResponse(newToken));
    }
}

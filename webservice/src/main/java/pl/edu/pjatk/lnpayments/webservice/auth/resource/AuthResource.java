package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.*;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;

import javax.validation.Valid;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.*;

@RestController
@RequestMapping(AUTH_PATH)
class AuthResource {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthResource(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping(REGISTER_PATH)
    ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(LOGIN_PATH)
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String email = request.getEmail();
        String jwtToken = jwtService.generateToken(email);
        LoginResponse loginResponse = userService.findAndConvertLoggedUser(email, jwtToken);

        return ResponseEntity.ok().body(loginResponse);
    }

    @GetMapping(REFRESH_PATH)
    ResponseEntity<RefreshTokenResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String newToken = jwtService.refreshToken(jwtService.headerToToken(authHeader).get());
        return ResponseEntity.ok(new RefreshTokenResponse(newToken));
    }

    @PostMapping(TEMPORARY_PATH)
    ResponseEntity<TemporaryAuthResponse> obtainTemporaryToken(@Valid @RequestBody TemporaryAuthRequest authRequest) {
        String email = authRequest.getEmail();
        String emailWithId = userService.createTemporaryUser(email);
        String jwtToken = jwtService.generateToken(emailWithId);
        TemporaryAuthResponse response = new TemporaryAuthResponse(emailWithId, jwtToken);
        return ResponseEntity.ok().body(response);
    }
}

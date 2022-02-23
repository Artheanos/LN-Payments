package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthResourceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthResource authResource;

    @Test
    void shouldReturnCreatedForCorrectRequest() {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "test");

        ResponseEntity<?> response = authResource.registerUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldLoginUserAndReturnResponseForCorrectData() {
        String email = "test@test.pl";
        LoginRequest request = new LoginRequest(email, "test");
        when(jwtService.generateToken(email)).thenReturn("token");

        ResponseEntity<LoginResponse> response = authResource.login(request);

        verify(jwtService).generateToken(email);
        verify(userService).findAndConvertLoggedUser(email, "token");
        verify(authenticationManager).authenticate(any());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}

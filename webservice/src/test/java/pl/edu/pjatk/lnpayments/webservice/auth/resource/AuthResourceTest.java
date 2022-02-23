package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthResourceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthResource authResource;

    @Test
    void shouldReturnCreatedForCorrectRequest() {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "test");

        ResponseEntity<?> response = authResource.registerUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldReturnOk() {
        String token = "token123";
        String newToken = "token321";

        when(jwtService.renewToken(token)).thenReturn(newToken);
        ResponseEntity<?> response = authResource.renewToken(String.format("Bearer %s", token));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(newToken);
    }
}

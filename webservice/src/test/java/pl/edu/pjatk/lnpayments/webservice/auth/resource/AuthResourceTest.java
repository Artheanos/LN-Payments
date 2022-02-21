package pl.edu.pjatk.lnpayments.webservice.auth.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthResourceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthResource authResource;

    @Test
    void shouldReturnCreatedForCorrectRequest() {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "test");

        ResponseEntity<?> response = authResource.registerUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

}

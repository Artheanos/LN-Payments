package pl.edu.pjatk.lnpayments.webservice.common.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.auth.service.UserService;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PasswordUpdateRequest;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserResource userResource;

    @Test
    void shouldReturnOkForDetailsGet() {
        UserDto dto = new UserDto();
        when(userService.getUserDetails("test")).thenReturn(dto);

        ResponseEntity<UserDto> details = userResource.getDetails(() -> "test");

        assertThat(details.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnOKForPasswordUpdate() {
        PasswordUpdateRequest req = new PasswordUpdateRequest();

        ResponseEntity<?> details = userResource.updatePassword(req, () -> "test");

        assertThat(details.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).updatePassword("test", req);
    }
}

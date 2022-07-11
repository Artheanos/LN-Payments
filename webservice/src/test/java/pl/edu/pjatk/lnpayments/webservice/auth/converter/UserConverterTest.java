package pl.edu.pjatk.lnpayments.webservice.auth.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.*;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserConverter userConverter;

    @Test
    void shouldConvertDtoToUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "pass");

        StandardUser user = userConverter.convertToEntity(request);

        assertThat(user.getEmail()).isEqualTo("test@test.pl");
        assertThat(user.getPassword()).isEqualTo("encoded_pass");
        assertThat(user.getFullName()).isEqualTo("test");
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void shouldConvertToUserDetails() {
        StandardUser user = new StandardUser("test@test.pl", "test", "pass");

        UserDetails userDetails = userConverter.convertToUserDetails(user);

        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().contains(Role.ROLE_USER)).isTrue();
    }

    @Test
    void shouldConvertToLoginResponse() {
        StandardUser user = new StandardUser("test@test.pl", "test", "pass");

        LoginResponse loginResponse = userConverter.convertToLoginResponse(user, "token");

        assertThat(loginResponse.getEmail()).isEqualTo(user.getEmail());
        assertThat(loginResponse.getFullName()).isEqualTo(user.getFullName());
        assertThat(loginResponse.getRole()).isEqualTo(user.getRole());
        assertThat(loginResponse.getToken()).isEqualTo("token");
        assertThat(loginResponse.getNotificationChannelId()).isNull();
    }

    @Test
    void shouldContainNotificationChannelIdWhenLoggingInAsAdmin() {
        StandardUser user = new AdminUser("test@test.pl", "test", "pass");

        LoginResponse loginResponse = userConverter.convertToLoginResponse(user, "token");

        assertThat(loginResponse.getEmail()).isEqualTo(user.getEmail());
        assertThat(loginResponse.getFullName()).isEqualTo(user.getFullName());
        assertThat(loginResponse.getRole()).isEqualTo(user.getRole());
        assertThat(loginResponse.getToken()).isEqualTo("token");
        assertThat(loginResponse.getNotificationChannelId().length()).isEqualTo(10);
    }

    @Test
    void shouldContainNullPasswordForNonStandardUser() {
        User user = new TemporaryUser("test@test.pl");

        UserDetails userDetails = userConverter.convertToUserDetails(user);

        assertThat(userDetails.getUsername()).startsWith(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(null);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().contains(Role.ROLE_TEMPORARY)).isTrue();
    }

    @Test
    void shouldConvertUserToUserDto() {
        StandardUser user = new StandardUser("test@test.pl", "test", "pass");

        UserDto dto = userConverter.convertToDto(user);

        assertThat(dto.getFullName()).isEqualTo(user.getFullName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        assertThat(dto.getCreatedAt()).isNotNull();
    }

}

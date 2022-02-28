package pl.edu.pjatk.lnpayments.webservice.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.edu.pjatk.lnpayments.webservice.auth.converter.StandardUserConverter;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.StandardUserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private StandardUserConverter standardUserConverter;

    @Mock
    private StandardUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveUserWhenEmailIsNotOccupied() {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "pass");
        StandardUser expectedUser = new StandardUser("test@test.pl", "test", "pass");
        when(standardUserConverter.convertToEntity(request)).thenReturn(expectedUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        userService.createUser(request);

        verify(standardUserConverter).convertToEntity(request);
        verify(userRepository).save(expectedUser);
        verify(userRepository).existsByEmail(request.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserExists() {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "pass");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() ->userService.createUser(request))
                .withMessage("User with mail test@test.pl exists!");
        verify(standardUserConverter, never()).convertToEntity(request);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnUserDetailsIfUserExists() {
        String email = "test@test.pl";
        StandardUser user = new StandardUser(email, "test", "pass");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDetails details = mockUserDetails(email, "pass", Role.ROLE_USER);
        when(standardUserConverter.convertToUserDetails(user)).thenReturn(details);

        UserDetails result = userService.loadUserByUsername(email);

        assertThat(result).isEqualTo(details);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String email = "test@test.pl";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername(email))
                .withMessage(email + " not found!");
        verify(standardUserConverter, never()).convertToUserDetails(any());
    }

    @Test
    void shouldReturnLoginRequestIfUserExists() {
        String email = "test@test.pl";
        String name = "test";
        String pass = "pass";
        String token = "token";
        StandardUser user = new StandardUser(email, name, pass);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        LoginResponse result = userService.findAndConvertLoggedUser(email, token);

        verify(userRepository).findByEmail(email);
        verify(standardUserConverter).convertToLoginResponse(user, token);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForLoggedUser() {
        String email = "test@test.pl";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername(email))
                .withMessage(email + " not found!");
        verify(standardUserConverter, never()).convertToLoginResponse(any(), anyString());
    }

    private UserDetails mockUserDetails(String email, String pass, Role role) {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singleton(role);
            }

            @Override
            public String getPassword() {
                return pass;
            }

            @Override
            public String getUsername() {
                return email;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }
}

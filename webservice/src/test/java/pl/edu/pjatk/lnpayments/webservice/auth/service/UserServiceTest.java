package pl.edu.pjatk.lnpayments.webservice.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pjatk.lnpayments.webservice.auth.converter.UserConverter;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.StandardUserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.UserRepository;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.LoginResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.resource.dto.RegisterRequest;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;
import pl.edu.pjatk.lnpayments.webservice.common.entity.StandardUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.TemporaryUser;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.PasswordUpdateRequest;
import pl.edu.pjatk.lnpayments.webservice.common.resource.dto.UserDto;

import javax.validation.ValidationException;
import java.time.Instant;
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
    private UserConverter userConverter;

    @Mock
    private StandardUserRepository standardUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveUserWhenEmailIsNotOccupied() {
        RegisterRequest request = new RegisterRequest("test@test.pl", "test", "pass");
        StandardUser expectedUser = new StandardUser("test@test.pl", "test", "pass");
        when(userConverter.convertToEntity(request)).thenReturn(expectedUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        userService.createUser(request);

        verify(userConverter).convertToEntity(request);
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
        verify(userConverter, never()).convertToEntity(request);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnUserDetailsIfUserExists() {
        String email = "test@test.pl";
        StandardUser user = new StandardUser(email, "test", "pass");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDetails details = mockUserDetails(email, "pass", Role.ROLE_USER);
        when(userConverter.convertToUserDetails(user)).thenReturn(details);

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
        verify(userConverter, never()).convertToUserDetails(any());
    }

    @Test
    void shouldThrowExceptionWhenLoggedUserNotFound() {
        String email = "test@test.pl";
        when(standardUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.findAndConvertLoggedUser(email, "token"))
                .withMessage(email + " not found!");
        verify(userConverter, never()).convertToLoginResponse(any(), anyString());
    }

    @Test
    void shouldReturnLoginRequestIfUserExists() {
        String email = "test@test.pl";
        String name = "test";
        String pass = "pass";
        String token = "token";
        StandardUser user = new StandardUser(email, name, pass);
        when(standardUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        LoginResponse result = userService.findAndConvertLoggedUser(email, token);

        verify(standardUserRepository).findByEmail(email);
        verify(userConverter).convertToLoginResponse(user, token);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForLoggedUser() {
        String email = "test@test.pl";
        TemporaryUser user = new TemporaryUser(email);
        when(userRepository.save(any())).thenReturn(user);

        String temporaryUserHashedEmail = userService.createTemporaryUser(email);

        assertThat(temporaryUserHashedEmail.length()).isEqualTo(email.length() + 9);
        assertThat(temporaryUserHashedEmail.split("#")[0]).isEqualTo(email);
        verify(userRepository).save(any(TemporaryUser.class));
    }

    @Test
    void shouldSaveTemporaryUser() {
        String email = "test@test.pl";

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername(email))
                .withMessage(email + " not found!");
        verify(userConverter, never()).convertToLoginResponse(any(), anyString());
    }

    @Test
    void shouldGetUserDetails() {
        String email = "test@test.pl";
        String name = "test";
        String pass = "pass";
        StandardUser user = new StandardUser(email, name, pass);
        UserDto dto = new UserDto(email, pass, Role.ROLE_USER, Instant.now());
        when(standardUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userConverter.convertToDto(user)).thenReturn(dto);

        UserDto userDetails = userService.getUserDetails(email);

        assertThat(userDetails).isEqualTo(dto);
    }

    @Test
    void shouldThrowExceptionWhenUserFromGetRequest() {
        String email = "test@test.pl";
        when(standardUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.getUserDetails(email))
                .withMessage(email + " not found!");
        verify(userConverter, never()).convertToDto(any());
    }

    @Test
    void shouldThrowExceptionWhenUserForPasswordChangeNotFound() {
        String email = "test@test.pl";
        when(standardUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.updatePassword(email, new PasswordUpdateRequest()))
                .withMessage(email + " not found!");
        verify(standardUserRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordsDoesNotMatch() {
        String email = "test@test.pl";
        String name = "test";
        String pass = "pass";
        StandardUser user = new StandardUser(email, name, pass);
        PasswordUpdateRequest updateRequest = new PasswordUpdateRequest("ddudu", "dududu");
        when(standardUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> userService.updatePassword(email, updateRequest))
                .withMessage("Wrong current password provided");
        verify(standardUserRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePasswordForCorrectData() {
        String email = "test@test.pl";
        String name = "test";
        String pass = "pass";
        String newPass = "dududu";
        StandardUser user = new StandardUser(email, name, pass);
        PasswordUpdateRequest updateRequest = new PasswordUpdateRequest(pass, newPass);
        when(standardUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(pass, pass)).thenReturn(true);

        userService.updatePassword(email, updateRequest);

        verify(passwordEncoder).encode(newPass);
        verify(standardUserRepository).save(any());
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

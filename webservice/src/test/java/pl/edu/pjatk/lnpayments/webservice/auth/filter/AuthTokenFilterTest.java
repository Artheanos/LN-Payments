package pl.edu.pjatk.lnpayments.webservice.auth.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.edu.pjatk.lnpayments.webservice.auth.service.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Test
    void shouldNotAuthenticateUserWhenNoTokenProvided() throws ServletException, IOException {
        String authHeader = "Bearer 2137";
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(jwtService.headerToToken(authHeader)).thenReturn(Optional.of("2137"));

        authTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(filterChain).doFilter(any(), any());
        verify(jwtService).isTokenValid(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void shouldAuthenticateUser() throws ServletException, IOException {
        String token = "2137";
        String authHeader = String.format("Bearer %s", token);
        String email = "test@test.pl";

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.retrieveEmail(token)).thenReturn(email);
        when(jwtService.headerToToken(authHeader)).thenReturn(Optional.of(token));

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(prepareUserDetails(email));

        authTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(filterChain).doFilter(any(), any());
        verify(jwtService).isTokenValid(anyString());
        verify(userDetailsService).loadUserByUsername(anyString());

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(userDetails.getUsername()).isEqualTo(email);
    }

    private UserDetails prepareUserDetails(String email) {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptyList();
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return email;
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };
    }
}

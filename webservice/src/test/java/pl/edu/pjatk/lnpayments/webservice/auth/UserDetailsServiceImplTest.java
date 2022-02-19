package pl.edu.pjatk.lnpayments.webservice.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

//TODO write proper tests when real service is implemented
class UserDetailsServiceImplTest {

    private final UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();

    @Test
    void shouldReturnNull() {
        UserDetails test = userDetailsService.loadUserByUsername("test");
        assertThat(test).isNull();
    }
}

package pl.edu.pjatk.lnpayments.webservice.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import pl.edu.pjatk.lnpayments.webservice.auth.filter.AuthTokenFilter;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;

import java.util.List;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.*;

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final static String[] UNSECURED_PATHS = addPrefixToAll(List.of(
            AUTH_PATH + REGISTER_PATH,
            AUTH_PATH + LOGIN_PATH,
            AUTH_PATH + TEMPORARY_PATH,
            PAYMENTS_PATH + INFO_PATH,
            PAYMENTS_WS_PATH,
            NOTIFICATION_WS_PATH
    ), API_PREFIX);

    private final static String[] ADMIN_PATHS = addPrefixToAll(List.of(
            AUTH_PATH + ADMIN_PATH,
            PAYMENTS_PATH + ALL_PATH,
            WALLET_PATH,
            TRANSACTIONS_PATH,
            NOTIFICATIONS_PATH,
            SETTINGS_PATH
    ), API_PREFIX);

    private final static String[] RESOURCE_PATHS = {
            "/v2/api-docs",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/assets/**",
            "/"
    };

    private final UserDetailsService userDetailsService;
    private final AuthTokenFilter jwtFilter;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService, AuthTokenFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(ADMIN_PATHS).hasAuthority(Role.ROLE_ADMIN.toString())
                .antMatchers(UNSECURED_PATHS).permitAll()
                .antMatchers(RESOURCE_PATHS).permitAll()
                .anyRequest().authenticated().and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    private static String[] addPrefixToAll(List<String> elements, String prefix) {
        return elements.stream()
                .map(element -> prefix + element)
                .toArray(String[]::new);
    }
}

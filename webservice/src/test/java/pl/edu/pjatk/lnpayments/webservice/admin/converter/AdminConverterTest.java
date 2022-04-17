package pl.edu.pjatk.lnpayments.webservice.admin.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.entity.Role;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminConverterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminConverter adminConverter;

    @Test
    void shouldConvertToAdminRequest() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        AdminRequest request = new AdminRequest("test@test.pl", "test", "pass");

        AdminUser user = adminConverter.convertToEntity(request);

        assertThat(user.getEmail()).isEqualTo("test@test.pl");
        assertThat(user.getPassword()).isEqualTo("encoded_pass");
        assertThat(user.getFullName()).isEqualTo("test");
        assertThat(user.hasKey()).isFalse();
        assertThat(user.isAssignedToWallet()).isFalse();
        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void shouldConvertAdminToDto() {
        AdminUser adminUser = AdminUser.adminBuilder()
                .email("test@test.pl")
                .fullName("test")
                .build();

        AdminResponse response = adminConverter.convertToDto(adminUser);

        assertThat(response.getEmail()).isEqualTo("test@test.pl");
        assertThat(response.getFullName()).isEqualTo("test");
    }

    @Test
    void shouldMapAllAdminsToPageOfResponses() {
        Page<AdminUser> payments = new PageImpl<>(List.of(
                new AdminUser("test1@test.pl", "test1", "test1"),
                new AdminUser("test2@test.pl", "test2", "test2"),
                new AdminUser("test3@test.pl", "test3", "test3")
        ));

        Page<AdminResponse> result = adminConverter.convertPageToDto(payments);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(AdminResponse::getFullName)
                .containsExactlyInAnyOrder("test1", "test2", "test3");
    }

    @Test
    void shouldCovertAllAdminsToListDto() {
        List<AdminUser> payments = List.of(
                new AdminUser("test1@test.pl", "test1", "test1"),
                new AdminUser("test2@test.pl", "test2", "test2"),
                new AdminUser("test3@test.pl", "test3", "test3")
        );

        List<AdminResponse> result = adminConverter.convertAllToDto(payments);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(AdminResponse::getFullName)
                .containsExactlyInAnyOrder("test1", "test2", "test3");
    }
}

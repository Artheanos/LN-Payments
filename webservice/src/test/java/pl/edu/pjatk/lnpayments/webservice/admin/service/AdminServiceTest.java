package pl.edu.pjatk.lnpayments.webservice.admin.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.edu.pjatk.lnpayments.webservice.admin.converter.AdminConverter;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;

import javax.validation.ValidationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminConverter adminConverter;

    @Mock
    private AdminUserRepository adminUserRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void shouldSaveAdminWhenEmailIsNotOccupied() {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "pass");
        AdminUser expectedUser = new AdminUser("test@test.pl", "test", "pass");
        when(adminConverter.convertToEntity(request)).thenReturn(expectedUser);
        when(adminUserRepository.existsByEmail(anyString())).thenReturn(false);

        adminService.createAdmin(request);

        verify(adminConverter).convertToEntity(request);
        verify(adminUserRepository).save(expectedUser);
        verify(adminUserRepository).existsByEmail(request.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserExists() {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "pass");
        when(adminUserRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> adminService.createAdmin(request))
                .withMessage("User with mail test@test.pl exists!");
        verify(adminConverter, never()).convertToEntity(request);
        verify(adminUserRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllAdmins() {
        AdminUser firstAdmin = new AdminUser("test1@test.pl", "test1", "pass");
        AdminUser secondAdmin = new AdminUser("test2@test.pl", "test2", "pass");
        AdminResponse convertedFirst = new AdminResponse("test1@test.pl", "test1");
        AdminResponse convertedSecond = new AdminResponse("test2@test.pl", "test2");
        PageImpl<AdminUser> adminEntities = new PageImpl<>(List.of(firstAdmin, secondAdmin));
        PageImpl<AdminResponse> adminDtos = new PageImpl<>(List.of(convertedFirst, convertedSecond));
        when(adminUserRepository.findAll(any(Pageable.class))).thenReturn(adminEntities);
        when(adminConverter.convertAllToDto(adminEntities)).thenReturn(adminDtos);

        Page<AdminResponse> response = adminService.findAllAdmins(PageRequest.ofSize(2));
        assertThat(response).isEqualTo(adminDtos);
        verify(adminConverter).convertAllToDto(adminEntities);
        verify(adminUserRepository).findAll(any(Pageable.class));
    }
}

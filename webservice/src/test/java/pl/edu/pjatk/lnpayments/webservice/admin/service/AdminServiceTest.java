package pl.edu.pjatk.lnpayments.webservice.admin.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.edu.pjatk.lnpayments.webservice.admin.converter.AdminConverter;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminDeleteRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        AdminResponse convertedFirst = new AdminResponse("test1@test.pl", "test1", false, false);
        AdminResponse convertedSecond = new AdminResponse("test2@test.pl", "test2", false, false);
        PageImpl<AdminUser> adminEntities = new PageImpl<>(List.of(firstAdmin, secondAdmin));
        PageImpl<AdminResponse> adminDtos = new PageImpl<>(List.of(convertedFirst, convertedSecond));
        when(adminUserRepository.findAll(any(Pageable.class))).thenReturn(adminEntities);
        when(adminConverter.convertPageToDto(adminEntities)).thenReturn(adminDtos);

        Page<AdminResponse> response = adminService.findAllAdmins(PageRequest.ofSize(2));
        assertThat(response).isEqualTo(adminDtos);
        verify(adminConverter).convertPageToDto(adminEntities);
        verify(adminUserRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnAdminFromEmails() {
        String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";
        List<String> emails = List.of("test1@test.pl", "test2@test.pl");
        AdminUser firstAdmin = new AdminUser(emails.get(0), "test1", "pass");
        firstAdmin.setPublicKey(publicKey);
        AdminUser secondAdmin = new AdminUser(emails.get(1), "test2", "pass");
        secondAdmin.setPublicKey(publicKey);
        when(adminUserRepository.findAllByEmailInAndPublicKeyNotNull(emails))
                .thenReturn(List.of(firstAdmin, secondAdmin));

        List<AdminUser> admins = adminService.findAllWithKeys(emails);

        assertThat(admins).hasSize(emails.size());
        assertThat(admins).extracting(AdminUser::getEmail).containsAll(emails);
        assertThat(admins).extracting(AdminUser::getPublicKey).allMatch(Objects::nonNull);
        verify(adminUserRepository).findAllByEmailInAndPublicKeyNotNull(emails);
    }

    @Test
    void shouldThrowExceptionWhenNotAllAdminsReturned() {
        List<String> emails = List.of("test1@test.pl", "test2@test.pl");
        AdminUser firstAdmin = new AdminUser(emails.get(0), "test1", "pass");
        when(adminUserRepository.findAllByEmailInAndPublicKeyNotNull(emails))
                .thenReturn(List.of(firstAdmin));

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> adminService.findAllWithKeys(emails))
                .withMessage("Not all users have uploaded their keys");
    }

    @Test
    void shouldUpdateKeyIfUserExistsAndKeyIsNotUploaded() {
        String email = "test@test.pl";
        String key = "2137";
        AdminUser user = new AdminUser(email, "test", "pass");
        when(adminUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        adminService.uploadKey(email, key);

        ArgumentCaptor<AdminUser> userCaptor = ArgumentCaptor.forClass(AdminUser.class);
        verify(adminUserRepository).findByEmail(email);
        verify(adminUserRepository).save(userCaptor.capture());
        AdminUser result = userCaptor.getValue();
        assertThat(result.hasKey()).isTrue();
        assertThat(result.getPublicKey()).isEqualTo(key);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String email = "test@test.pl";
        when(adminUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> adminService.uploadKey(email, "1"))
                .withMessage(email + " not found!");
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasKeys() {
        String email = "test@test.pl";
        String key = "2137";
        AdminUser user = new AdminUser(email, "test", "pass");
        user.setPublicKey(key);
        when(adminUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> adminService.uploadKey(email, "1"))
                .withMessage("User has already uploaded his keys!");
    }

    @Test
    void shouldThrowExceptionWhenUserWithThatMailNotExist() {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "pass");
        AdminDeleteRequest getEmail = new AdminDeleteRequest(request.getEmail());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> adminService.removeAdmin(getEmail))
                .withMessage(getEmail.getEmail() + " not found!");

        verify(adminUserRepository, never()).delete(any());
    }

    @Test
    void shouldRemoveUserWhoIsNotAddedToWallet() {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "pass");
        AdminUser expectedUser = new AdminUser("test@test.pl", "test", "pass");
        AdminDeleteRequest getEmail = new AdminDeleteRequest(request.getEmail());

        when(adminUserRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(expectedUser));
        assertThat(expectedUser.isAssignedToWallet()).isFalse();

        adminService.removeAdmin(getEmail);

        verify(adminUserRepository).delete(expectedUser);
    }

    @Test
    void shouldThrowExceptionWhenUserIsAddedToWallet() {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "pass");
        AdminUser expectedUser = new AdminUser("test@test.pl", "test", "pass");
        Wallet wallet = new Wallet();
        AdminDeleteRequest getEmail = new AdminDeleteRequest(request.getEmail());

        expectedUser.setWallet(wallet);
        when(adminUserRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(expectedUser));
        assertThat(expectedUser.isAssignedToWallet()).isTrue();

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> adminService.removeAdmin(getEmail))
                .withMessage("Admin " + getEmail.getEmail() +" is already added to wallet");

        verify(adminUserRepository, never()).delete(any());
    }

}

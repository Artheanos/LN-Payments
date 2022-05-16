package pl.edu.pjatk.lnpayments.webservice.admin.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminDeleteRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.KeyUploadRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminResourceTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminResource adminResource;

    @Test
    void shouldReturnCreatedWhenUserCreated() {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "test");

        ResponseEntity<?> response = adminResource.addAdmin(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(adminService).createAdmin(request);
    }

    @Test
    void shouldReturnOkForAdminsRequest() {
        ResponseEntity<Page<AdminResponse>> response = adminResource.getAdmins(Pageable.unpaged());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(adminService).findAllAdmins(Pageable.unpaged());
    }

    @Test
    void shouldReturnOkForUploadKeys() {
        KeyUploadRequest uploadRequest = new KeyUploadRequest("2137");

        ResponseEntity<?> response = adminResource.uploadPublicKey(uploadRequest, () -> "email");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(adminService).uploadKey("email", "2137");
    }

    @Test
    void shouldReturnOkWhenUserRemoved() {
        AdminRequest request = new AdminRequest("test@test.pl", "test", "test");
        AdminDeleteRequest getEmail = new AdminDeleteRequest(request.getEmail());

        adminService.createAdmin(request);
        ResponseEntity<?> responseDelete = adminResource.deleteAdmin(getEmail);

        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(adminService).removeAdmin(getEmail);
    }

}

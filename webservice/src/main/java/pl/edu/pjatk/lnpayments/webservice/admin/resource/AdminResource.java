package pl.edu.pjatk.lnpayments.webservice.admin.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminDeleteRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.KeyUploadRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;

import javax.validation.Valid;

import java.security.Principal;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.ADMIN_PATH;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.KEYS_PATH;

@RestController
@RequestMapping(ADMIN_PATH)
class AdminResource {

    private final AdminService userService;

    @Autowired
    public AdminResource(AdminService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addAdmin(@RequestBody @Valid AdminRequest adminRequest) {
        userService.createAdmin(adminRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<AdminResponse>> getAdmins(Pageable pageable) {
        Page<AdminResponse> admins = userService.findAllAdmins(pageable);
        return ResponseEntity.ok(admins);
    }

    @PatchMapping(KEYS_PATH)
    public ResponseEntity<?> uploadPublicKey(@RequestBody @Valid KeyUploadRequest keyUploadRequest,
                                             Principal principal) {
        userService.uploadKey(principal.getName(), keyUploadRequest.getPublicKey());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAdmin(@RequestBody @Valid AdminDeleteRequest adminDeleteRequest) {
        userService.removeAdmin(adminDeleteRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

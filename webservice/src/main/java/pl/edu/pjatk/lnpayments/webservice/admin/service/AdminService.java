package pl.edu.pjatk.lnpayments.webservice.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.admin.converter.AdminConverter;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminDeleteRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.List;

@Service
public class AdminService {

    private final AdminUserRepository adminUserRepository;
    private final AdminConverter adminConverter;

    @Autowired
    public AdminService(AdminUserRepository adminUserRepository, AdminConverter adminConverter) {
        this.adminUserRepository = adminUserRepository;
        this.adminConverter = adminConverter;
    }

    @Transactional
    public void createAdmin(AdminRequest request) {
        String requestEmail = request.getEmail();
        if (adminUserRepository.existsByEmail(requestEmail)) {
            throw new ValidationException("User with mail " + requestEmail + " exists!");
        }
        AdminUser user = adminConverter.convertToEntity(request);
        adminUserRepository.save(user);
    }

    public Page<AdminResponse> findAllAdmins(Pageable pageable) {
        Page<AdminUser> admins = adminUserRepository.findAll(pageable);
        return adminConverter.convertPageToDto(admins);
    }

    public List<AdminUser> findAllWithKeys(List<String> emails) {
        List<AdminUser> adminUsers = adminUserRepository.findAllByEmailInAndPublicKeyNotNull(emails);
        if (adminUsers.size() != emails.size()) {
            throw new InconsistentDataException("Not all users have uploaded their keys");
        }
        return adminUsers;
    }

    public void uploadKey(String email, String publicKey) {
        AdminUser admin = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found!"));
        if (admin.hasKey()) {
            throw new ValidationException("User has already uploaded his keys!");
        }
        admin.setPublicKey(publicKey);
        adminUserRepository.save(admin);
    }

    public void removeAdmin(AdminDeleteRequest deleteRequest) {
        String requestEmail = deleteRequest.getEmail();
        AdminUser admin = adminUserRepository.findByEmail(requestEmail)
                .orElseThrow(() -> new UsernameNotFoundException(requestEmail + " not found!"));
        if (admin.isAssignedToWallet()) {
            throw new ValidationException("Admin " + requestEmail +" is already added to wallet");
        }
        adminUserRepository.delete(admin);
    }
}

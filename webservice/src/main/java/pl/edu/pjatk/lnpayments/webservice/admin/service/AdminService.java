package pl.edu.pjatk.lnpayments.webservice.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.admin.converter.AdminConverter;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminResponse;
import pl.edu.pjatk.lnpayments.webservice.auth.repository.AdminUserRepository;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

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
        return adminConverter.convertAllToDto(admins);
    }
}

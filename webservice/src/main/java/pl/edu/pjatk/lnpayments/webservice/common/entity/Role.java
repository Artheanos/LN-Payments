package pl.edu.pjatk.lnpayments.webservice.common.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_USER, ROLE_ADMIN, ROLE_TEMPORARY;

    @Override
    public String getAuthority() {
        return toString();
    }

}

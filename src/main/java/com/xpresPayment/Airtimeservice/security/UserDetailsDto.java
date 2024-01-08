package com.xpresPayment.Airtimeservice.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class UserDetailsDto{

    private Collection<? extends GrantedAuthority> grantedAuthorities;
    private String username;
    public boolean accountNonExpired;
    public boolean accountNonLocked;
    public boolean credentialsNonExpired;
    private boolean enabled;
    private String fullName;
    private String phoneNumber;
}

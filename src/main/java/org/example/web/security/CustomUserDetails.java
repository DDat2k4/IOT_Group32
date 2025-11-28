package org.example.web.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String username, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public boolean hasRole(String roleName) {
        if (roleName == null) return false;
        String normalized = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase(normalized));
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
//
//    @Override
//    public boolean isAccountNonExpired() { return active; }
//
//    @Override
//    public boolean isAccountNonLocked() { return active; }
//
//    @Override
//    public boolean isCredentialsNonExpired() { return active; }
//
//    @Override
//    public boolean isEnabled() { return active; }
}

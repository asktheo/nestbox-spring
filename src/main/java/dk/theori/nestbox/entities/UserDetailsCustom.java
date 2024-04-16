package dk.theori.nestbox.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * author: Zeeshan Adil, <a href="https://www.linkedin.com/in/zeeshan-adil-a94b3867/">...</a>
 * <a href="https://medium.com/spring-boot/spring-boot-3-spring-security-6-jwt-authentication-authorization-98702d6313a5">...</a>
 */

public class UserDetailsCustom extends UserInfo implements UserDetails {

    private String usernameCustom;
    private String passwordCustom;
    Collection<? extends GrantedAuthority> authorities;

    public UserDetailsCustom(UserInfo byUsername) {
        this.usernameCustom = byUsername.getUsername();
        this.passwordCustom = byUsername.getPassword();
        List<GrantedAuthority> auths = new ArrayList<>();

        for(UserRole role : byUsername.getRoles()){

            auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
        }
        this.authorities = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordCustom;
    }

    @Override
    public String getUsername() {
        return usernameCustom;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

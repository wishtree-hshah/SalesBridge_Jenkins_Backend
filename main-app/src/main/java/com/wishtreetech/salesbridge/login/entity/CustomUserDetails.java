package com.wishtreetech.salesbridge.login.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * The type Custom user details.
 */
public class CustomUserDetails implements UserDetails {
    private final User user;

    /**
     * Instantiates a new Custom user details.
     *
     * @param user the user
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Authority> authorities = user.getRole().getAuthorities();
        return authorities.stream().map(
                authority -> new SimpleGrantedAuthority(authority.getName().name())
        ).toList();
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return null; // Assuming authentication is done via OAuth
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Is account non expired boolean.
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Is account non locked boolean.
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Is credentials non expired boolean.
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }
}

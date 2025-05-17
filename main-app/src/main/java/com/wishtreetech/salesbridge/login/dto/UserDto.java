package com.wishtreetech.salesbridge.login.dto;

import com.wishtreetech.salesbridge.login.entity.RoleEnum;

/**
 * The type User dto.
 */
public class UserDto {
    private String email;
    private String name;
    private RoleEnum role;

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets role.
     *
     * @return the role
     */
    public RoleEnum getRole() {
        return role;
    }

    /**
     * Sets role.
     *
     * @param role the role
     */
    public void setRole(RoleEnum role) {
        this.role = role;
    }
}

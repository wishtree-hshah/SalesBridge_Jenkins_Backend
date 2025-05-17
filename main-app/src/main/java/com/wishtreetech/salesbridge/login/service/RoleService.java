package com.wishtreetech.salesbridge.login.service;

import com.wishtreetech.salesbridge.login.entity.AuthorityEnum;
import com.wishtreetech.salesbridge.login.entity.Role;
import com.wishtreetech.salesbridge.login.entity.RoleEnum;
import org.springframework.stereotype.Service;

/**
 * The interface Role service.
 */
@Service
public interface RoleService {
    /**
     * Add role.
     *
     * @param role the role
     * @return
     */
    Role addRole(RoleEnum role);

    /**
     * Delete role.
     *
     * @param role the role
     */
    void deleteRole(RoleEnum role);

    /**
     * Gets role.
     *
     * @param role the role
     * @return
     */
    Role getRole(RoleEnum role);

    /**
     * Add authority.
     *
     * @return
     */
    Role addAuthority(AuthorityEnum authorityEnum, Role role);

}

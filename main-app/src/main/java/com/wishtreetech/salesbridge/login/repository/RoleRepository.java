package com.wishtreetech.salesbridge.login.repository;

import com.wishtreetech.salesbridge.login.entity.Role;
import com.wishtreetech.salesbridge.login.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface Role repository.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Find by name role.
     *
     * @param name the name
     * @return the role
     */
    Role findByName(RoleEnum name);
}

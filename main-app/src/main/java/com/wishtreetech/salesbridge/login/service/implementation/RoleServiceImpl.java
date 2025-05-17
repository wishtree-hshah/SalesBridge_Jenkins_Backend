package com.wishtreetech.salesbridge.login.service.implementation;

import com.wishtreetech.salesbridge.login.entity.Authority;
import com.wishtreetech.salesbridge.login.entity.AuthorityEnum;
import com.wishtreetech.salesbridge.login.entity.Role;
import com.wishtreetech.salesbridge.login.entity.RoleEnum;
import com.wishtreetech.salesbridge.login.repository.RoleRepository;
import com.wishtreetech.salesbridge.login.service.AuthorityService;
import com.wishtreetech.salesbridge.login.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Role service.
 */
@Service
public class RoleServiceImpl implements RoleService {

    /**
     * The Role repository.
     */
    @Autowired
    RoleRepository roleRepository;

    /**
     * The Authority service.
     */
    @Autowired
    AuthorityService authorityService;
    /**
     * Add role if it does not exist.
     *
     * @return The persisted Role entity
     */
    @Override
    @Transactional
    public Role addRole(RoleEnum roleEnum) {
        Role existingRole = roleRepository.findByName(roleEnum);
        if (existingRole != null) {
            return existingRole; // Return the existing managed entity
        }
        return roleRepository.save(new Role(roleEnum)); // Save and return managed entity
    }

    /**
     * Delete role.
     */
    @Override
    public void deleteRole(RoleEnum role) {
        Role role1 = roleRepository.findByName(role);
        if (role1 != null) {
            roleRepository.delete(role1);
        }
    }

    /**
     * Get role, ensuring it is a managed entity.
     */
    @Override
    public Role getRole(RoleEnum role) {
        return roleRepository.findByName(role);
    }

    /**
     * Add authority.
     *
     * @param authorityEnum
     * @param role
     * @return
     */
    @Override
    @Transactional
    public Role addAuthority(AuthorityEnum authorityEnum, Role role) {
        Authority authority = authorityService.getAuthority(authorityEnum);
        if(role.getAuthorities().stream().filter(a -> a.getName().equals(authorityEnum)).findFirst().isPresent()){
            return role;
        }
        role.getAuthorities().add(authority);
        return roleRepository.save(role);
    }

}

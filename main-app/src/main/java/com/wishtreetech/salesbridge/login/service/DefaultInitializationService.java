package com.wishtreetech.salesbridge.login.service;

import com.wishtreetech.salesbridge.login.entity.AuthorityEnum;
import com.wishtreetech.salesbridge.login.entity.Role;
import com.wishtreetech.salesbridge.login.entity.RoleEnum;
import com.wishtreetech.salesbridge.login.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The type Default initialization service.
 */
@Service
public class DefaultInitializationService {
    private final UserService userService;
    private final RoleService roleService;
    private final AuthorityService authorityService;

    @Value("${spring.default.admin.email}")
    private String defaultAdminEmail;

    @Value("${spring.default.admin.name}")
    private String defaultAdminName;

    /**
     * Instantiates a new Default initialization service.
     *
     * @param userService the user service
     * @param roleService the role service
     */
    @Autowired
    public DefaultInitializationService(UserService userService, RoleService roleService, AuthorityService authorityService) {
        this.userService = userService;
        this.roleService = roleService;
        this.authorityService = authorityService;
    }

    /**
     * Create default user.
     */
    @PostConstruct
    public void createDefaultUser() {
        createDefaultRoles();
        saveAuthorities();
        mapAuthoritiesToRole();
        // Check if user already exists
        try {
            if (userService.getUserByEmail(defaultAdminEmail).isEmpty()) {
                Role adminRole = roleService.getRole(RoleEnum.ROLE_ADMIN); // Ensure role is persisted
                User adminUser = new User();
                adminUser.setEmail(defaultAdminEmail);
                adminUser.setRole(adminRole);
                adminUser.setName(defaultAdminName);
                adminUser.setActive(true);

                userService.addUser(adminUser);
                System.out.println("✅ Default admin user created: " + defaultAdminEmail);
            } else {
                System.out.println("ℹ️ Default admin user already exists.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error creating default admin user: " + e.getMessage());
        }
    }

    /**
     * Create default roles which are available in RoleEnum.
     */
    public void createDefaultRoles(){
        for(RoleEnum roleEnum : RoleEnum.values()){
            roleService.addRole(roleEnum); // Ensure each role is persisted
            System.out.println("✅ Default role ensured: " + roleEnum);
        }
    }

    /**
     * get Authorities for each role.
     */
    public Map<RoleEnum, Set<AuthorityEnum>> getMapedAuthority(){
        Map<RoleEnum, Set<AuthorityEnum>> roleEnumSetMap = new HashMap<>();

        // Mapping for ROLE_ADMIN (assigning all authority actions)
        roleEnumSetMap.put(RoleEnum.ROLE_ADMIN, EnumSet.of(
                AuthorityEnum.CREATE_USER,
                AuthorityEnum.READ_USER,
                AuthorityEnum.UPDATE_USER,
                AuthorityEnum.DELETE_USER,
                AuthorityEnum.READ_ALL_USER,
                AuthorityEnum.MANAGE_ROCKETREACH_CREDENTIAL,
                AuthorityEnum.ROCKETREACH_ACCESS,
                AuthorityEnum.VERIFY_EMAIL
        ));

        // Mapping for ROLE_USER (assigning limited authority actions)
        roleEnumSetMap.put(RoleEnum.ROLE_USER, EnumSet.of(
                AuthorityEnum.READ_USER,
                AuthorityEnum.ROCKETREACH_ACCESS,
                AuthorityEnum.VERIFY_EMAIL
        ));

        return roleEnumSetMap;
    }

    public void saveAuthorities(){
        for(AuthorityEnum authorityEnum : AuthorityEnum.values()){
            authorityService.createAuthority(authorityEnum); // Ensure each authority is persisted
            System.out.println("✅ Authority ensured: " + authorityEnum);
        }
    }

    /**
     * Save authorities for each role.
     */
    @Transactional
    public void mapAuthoritiesToRole() {
        Map<RoleEnum, Set<AuthorityEnum>> roleEnumSetMap = getMapedAuthority();
        for (Map.Entry<RoleEnum, Set<AuthorityEnum>> entry : roleEnumSetMap.entrySet()) {
            Role role = roleService.getRole(entry.getKey());
            Set<AuthorityEnum> authorityEnums = entry.getValue();
            for (AuthorityEnum authorityEnum : authorityEnums) {
                role = roleService.addAuthority(authorityEnum, role);
                System.out.println("✅ Authority ensured: " + authorityEnum + " for role: " + role.getName());
            }
        }
    }
}

package com.wishtreetech.salesbridge.login.service;

import com.wishtreetech.salesbridge.login.entity.Authority;
import com.wishtreetech.salesbridge.login.entity.AuthorityEnum;

/**
 * The interface Authority service.
 */
public interface AuthorityService {
    /**
     * Create authority.
     *
     * @param authority the authority
     * @return the authority
     */
    Authority createAuthority(AuthorityEnum authority);

    /**
     * Gets authority.
     *
     * @param name the name
     * @return the authority
     */
    Authority getAuthority(AuthorityEnum name);

    /**
     * Delete authority.
     *
     * @param name the name
     */
    void deleteAuthority(AuthorityEnum name);

}
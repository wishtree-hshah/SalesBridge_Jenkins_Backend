package com.wishtreetech.salesbridge.login.service.implementation;

import com.wishtreetech.salesbridge.login.entity.Authority;
import com.wishtreetech.salesbridge.login.entity.AuthorityEnum;
import com.wishtreetech.salesbridge.login.repository.AuthorityRepository;
import com.wishtreetech.salesbridge.login.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The type Authority service.
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    /**
     * The Authority repository.
     */
    @Autowired
    AuthorityRepository authorityRepository;

    /**
     * Create authority authority.
     *
     * @param authority the authority
     * @return the authority
     */
    @Override
    public Authority createAuthority(AuthorityEnum authority) {
        Authority existingAuthority = authorityRepository.findByName(authority);
        if (existingAuthority != null) {
            return existingAuthority;
        }
        return authorityRepository.save(new Authority(authority));
    }

    /**
     * Gets authority.
     *
     * @param name the name
     * @return the authority
     */
    @Override
    public Authority getAuthority(AuthorityEnum name) {
        return authorityRepository.findByName(name);
    }

    /**
     * Delete authority.
     *
     * @param name the name
     */
    @Override
    public void deleteAuthority(AuthorityEnum name) {
        Authority authority = authorityRepository.findByName(name);
        authorityRepository.delete(authority);
    }
}

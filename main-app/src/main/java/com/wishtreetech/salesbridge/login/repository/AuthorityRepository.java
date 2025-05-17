package com.wishtreetech.salesbridge.login.repository;

import com.wishtreetech.salesbridge.login.entity.Authority;
import com.wishtreetech.salesbridge.login.entity.AuthorityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface Authority repository.
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    /**
     * Find by name authority.
     *
     * @param name the name
     * @return the authority
     */
    Authority findByName(AuthorityEnum name);
}

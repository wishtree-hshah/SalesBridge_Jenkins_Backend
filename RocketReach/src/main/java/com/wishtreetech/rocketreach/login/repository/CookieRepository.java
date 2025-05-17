package com.wishtreetech.rocketreach.login.repository;

import com.wishtreetech.rocketreach.login.entity.Cookie;
import com.wishtreetech.rocketreach.login.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Cookie repository.
 */
public interface CookieRepository extends JpaRepository<Cookie, Long> {

    /**
     * Finds a cookie by its name and associated credential.
     * @param name the name of the cookie
     * @param credential the credential associated with the cookie
     * @return an Optional containing the Cookie if found, or empty if not
     */
    Optional<Cookie> findByNameAndCredential(String name, Credential credential);

    /**
     * Finds all cookies associated with a specific credential.
     * @param credential the credential
     * @return a list of cookies associated with the credential
     */
    List<Cookie> findByCredential(Credential credential);

    /**
     * Deletes cookies associated with a specific credential.
     * @param credential the credential
     */
    void deleteByCredential(Credential credential);
}

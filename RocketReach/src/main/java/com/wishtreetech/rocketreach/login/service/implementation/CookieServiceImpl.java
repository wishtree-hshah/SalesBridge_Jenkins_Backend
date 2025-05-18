package com.wishtreetech.rocketreach.login.service.implementation;

import com.wishtreetech.rocketreach.login.entity.Cookie;
import com.wishtreetech.rocketreach.login.entity.Credential;
import com.wishtreetech.rocketreach.login.repository.CookieRepository;
import com.wishtreetech.rocketreach.login.service.CookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * The implementation of the CookieService interface.
 */
@Service
public class CookieServiceImpl implements CookieService {

    private final CookieRepository cookieRepository;

    /**
     * Instantiates a new Cookie service.
     *
     * @param cookieRepository the cookie repository
     */
    @Autowired
    public CookieServiceImpl(CookieRepository cookieRepository) {
        this.cookieRepository = cookieRepository;
    }

    /**
     * Gets a cookie by its name and associated credential.
     *
     * @param key the key (cookie name)
     * @param credential the credential associated with the cookie
     * @return the cookie if found, null otherwise
     */
    @Override
    public Cookie getCookie(String key, Credential credential) {
        return cookieRepository.findByNameAndCredential(key, credential).orElse(null);
    }

    /**
     * Updates or creates a cookie.
     *
     * @param cookie the cookie
     * @return the updated or created cookie
     */
    @Override
    public Cookie updateCookie(Cookie cookie) {
        return cookieRepository.findByNameAndCredential(cookie.getName(), cookie.getCredential()).map(existingCookie -> {
            existingCookie.setValue(cookie.getValue());
            existingCookie.setDomain(cookie.getDomain());
            existingCookie.setPath(cookie.getPath());
            existingCookie.setExpiry(cookie.getExpiry());
            return cookieRepository.save(existingCookie);
        }).orElseGet(() -> cookieRepository.save(cookie));
    }

    /**
     * Validates a cookie by its expiry date.
     *
     * @param cookie the cookie
     * @return true if the cookie is valid, false otherwise
     */
    @Override
    public boolean validate(Cookie cookie) {
        if (cookie == null) {
            return false;
        }
        return cookie.getExpiry() != null && cookie.getExpiry().after(new Date());
    }

    /**
     * Gets all cookies associated with a particular credential.
     *
     * @param credential the credential associated with the cookies
     * @return a list of cookies for the specified credential
     */
    @Override
    public List<Cookie> getAllCookies(Credential credential) {
        return cookieRepository.findByCredential(credential);
    }

    /**
     * Deletes all cookies associated with a particular credential.
     *
     * @param credential the credential
     */
    @Override
    public void deleteAllCookies(Credential credential) {
        cookieRepository.deleteByCredential(credential);
    }
}

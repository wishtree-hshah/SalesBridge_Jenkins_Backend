package com.wishtreetech.rocketreach.login.service;

import com.wishtreetech.rocketreach.login.entity.Cookie;
import com.wishtreetech.rocketreach.login.entity.Credential;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The interface Cookie service.
 */
@Service
public interface CookieService {
    /**
     * Gets cookie.
     *
     * @param key the key
     */
    Cookie getCookie(String key, Credential credential);

    /**
     * Update cookie.
     *
     * @param cookie the cookie
     */
    Cookie updateCookie(Cookie cookie);

    /**
     * Validate cokkie by expiry date.
     *
     * @param cookie the cookie
     */
    boolean validate(Cookie cookie);

    /**
     * Get all cookies.
     */
    List<Cookie> getAllCookies(Credential credential);

    /**
     * Delete all cookie.
     */
    void deleteAllCookies(Credential credential);
}

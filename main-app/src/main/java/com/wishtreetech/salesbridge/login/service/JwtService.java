package com.wishtreetech.salesbridge.login.service;

import com.wishtreetech.salesbridge.login.entity.User;

import java.util.Date;
import java.util.List;

/**
 * The interface Jwt service.
 */
public interface JwtService {
    /**
     * Extract email string.
     *
     * @param token the token
     * @return the string
     */
    String extractEmail(String token);

    /**
     * Generate token string.
     *
     * @param user the user
     * @return the string
     */
    String generateToken(User user);

    /**
     * Is token valid boolean.
     *
     * @param token       the token
     * @return the boolean
     */
    boolean isTokenValid(String token);

    /**
     * Gets expiration time.
     *
     * @return the expiration time
     */
    long getExpirationTime();

    List<String> extractAuthorities(String jwt);

    Date extractExpiration(String token);

    Date getIssuedAt(String token);
}

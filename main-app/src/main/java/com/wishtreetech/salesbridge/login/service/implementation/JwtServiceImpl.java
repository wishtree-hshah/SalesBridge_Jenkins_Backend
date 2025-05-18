package com.wishtreetech.salesbridge.login.service.implementation;

import com.wishtreetech.salesbridge.login.entity.CustomUserDetails;
import com.wishtreetech.salesbridge.login.entity.User;
import com.wishtreetech.salesbridge.login.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The type Jwt service.
 */
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    /**
     * Extract all claims claims.
     *
     * @param token the token
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception exception) {
            logger.error("Error parsing token: {}", exception.getMessage());
            throw exception;
        }
    }

    /**
     * Gets sign key.
     *
     * @return the sign key
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract email string.
     * @param token
     * @return
     */
    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract claim t.
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate token string.
     * @param user
     * @return
     */
    @Override
    public String generateToken(User user) {
        UserDetails userDetails = new CustomUserDetails(user);
        HashMap extraClaims = new HashMap();
        extraClaims.put("authorities", userDetails.getAuthorities());
        return generateToken(extraClaims, userDetails);
    }

    /**
     * Generate token string.
     * @param extraClaims
     * @param userDetails
     * @return
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Is token valid boolean.
     * @param token
     * @return
     */
    @Override
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Is token expired boolean.
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date.
     * @param token
     * @return
     */
    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Date getIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Build token string.
     * @param extraClaims
     * @param userDetails
     * @param jwtExpiration
     * @return
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey()) // Updated signing method
                .compact();
    }

    /**
     * Gets expiration time.
     * @return
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    @Override
    public List<String> extractAuthorities(String jwt) {
        Map map = extractAllClaims(jwt);
        return ((List<Map>) map.get("authorities")).stream().map(authority -> authority.get("authority").toString()).toList();
    }
}

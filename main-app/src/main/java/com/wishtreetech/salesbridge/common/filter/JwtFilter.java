package com.wishtreetech.salesbridge.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wishtreetech.commonutils.dto.ErrorResponse;
import com.wishtreetech.salesbridge.login.entity.User;
import com.wishtreetech.salesbridge.login.service.JwtService;
import com.wishtreetech.salesbridge.login.service.UserService;
import com.wishtreetech.salesbridge.login.service.implementation.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * The type Jwt filter.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    /**
     * Instantiates a new Jwt filter.
     *
     * @param jwtService         the jwt service
     * @param userDetailsService the user details service
     */
    @Autowired
    public JwtFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, UserService userService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie jwtCookie = Arrays.stream(cookies)
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .findFirst()
                .orElse(null);

        if (jwtCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt = jwtCookie.getValue();
        if (authentication instanceof OAuth2AuthenticationToken || authentication == null) {
            try {
                if (jwtService.isTokenValid(jwt)) {
                    String email = jwtService.extractEmail(jwt);
                    Optional<User> optionalUser = userService.getUserByEmail(email);

                    if (optionalUser.isEmpty()) {
                        sendErrorResponse(response, "User not found", HttpServletResponse.SC_NOT_FOUND, request);
                        return;
                    }

                    User user = optionalUser.get();
                    // Check if the user's updatedAt is after the JWT issued time
                    if (user.getUpdatedAt().after(jwtService.getIssuedAt(jwt))) {
                        removeJwtCookie(response);
                        filterChain.doFilter(request, response);
                    }

                    List<String> authorities = jwtService.extractAuthorities(jwt);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            email, // Set email as principal
                            null,
                            authorities.stream().map(SimpleGrantedAuthority::new).toList()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                }

            } catch (ExpiredJwtException expiredJwtException) {
                removeJwtCookie(response);
                sendErrorResponse(response, "JWT Token has expired", HttpServletResponse.SC_UNAUTHORIZED, request);
                return;
            } catch (Exception e) {
                sendErrorResponse(response, "Invalid JWT Token", HttpServletResponse.SC_UNAUTHORIZED, request);
                return;
            }
        } else {
            String email = (String) authentication.getPrincipal();
            Optional<User> optionalUser = userService.getUserByEmail(email);

            if (optionalUser.isEmpty()) {
                sendErrorResponse(response, "User not found", HttpServletResponse.SC_NOT_FOUND, request);
                return;
            }

            User user = optionalUser.get();
            // Check if the user's updatedAt is after the JWT issued time
            if (user.getUpdatedAt().after(jwtService.getIssuedAt(jwt)) ) {
                removeJwtCookie(response);
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Sends a global error response using ErrorResponseDto.
     *
     * @param response  The HTTP response
     * @param message   The error message
     * @param status    The HTTP status code
     * @throws IOException if an I/O error occurs
     */
    private void sendErrorResponse(HttpServletResponse response, String message, int status, HttpServletRequest request) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status,
                "Unauthorized",
                message,
                request.getRequestURI()
        );
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Removes the JWT cookie from the response.
     *
     * @param response The HTTP response
     */
    private void removeJwtCookie(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setMaxAge(0); // Immediately expire the cookie
        jwtCookie.setPath("/");

        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");

        response.addCookie(jwtCookie);
        response.addCookie(sessionCookie);
    }
}
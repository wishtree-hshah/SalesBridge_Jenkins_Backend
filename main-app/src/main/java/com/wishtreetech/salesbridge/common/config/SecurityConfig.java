package com.wishtreetech.salesbridge.common.config;

import com.wishtreetech.salesbridge.common.filter.JwtFilter;
import com.wishtreetech.salesbridge.login.oauth.OAuthSuccessHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * The type Security config.
 */
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final JwtFilter jwtFilter;

    /**
     * Instantiates a new Security config.
     *
     * @param oAuthSuccessHandler the o auth success handler
     * @param jwtFilter           the jwt filter
     */
    @Autowired
    public SecurityConfig(OAuthSuccessHandler oAuthSuccessHandler, JwtFilter jwtFilter) {
        this.oAuthSuccessHandler = oAuthSuccessHandler;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Security filter chain security filter chain.
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/api/login").permitAll() // Public APIs
                        .anyRequest().permitAll() // All other requests require authentication
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuthSuccessHandler)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler()) // 401 Handler
                        .accessDeniedHandler(accessDeniedHandler()) // 403 Handler
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            Cookie jwtCookie = new Cookie("jwt", null);
                            jwtCookie.setMaxAge(0);
                            jwtCookie.setPath("/");
                            response.addCookie(jwtCookie);
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                );
        return http.build();
    }

    /**
     * Custom 401 Unauthorized Handler
     *
     * @return the authentication entry point
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "401 Unauthorized: Invalid credentials or missing token.");
        };
    }

    /**
     * Custom 403 Forbidden Handler
     *
     * @return the access denied handler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "403 Forbidden: You do not have the required permissions.");
        };
    }

    /**
     * Password encoder password encoder.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cors configuration source cors configuration source.
     *
     * @return the cors configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Frontend URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true); // Allow credentials (cookies)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Allowed headers
        configuration.setExposedHeaders(List.of("Authorization")); // Expose JWT token if needed
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

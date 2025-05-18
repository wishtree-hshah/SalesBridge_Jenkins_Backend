package com.wishtreetech.salesbridge.login.oauth;

import com.wishtreetech.salesbridge.login.service.JwtService;
import com.wishtreetech.salesbridge.login.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
/**
 * The type O auth success handler.
 */
@Service
public class OAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /**
     * The Redirection url.
     */
    @Value("${oauth2.redirect-url}")
    String redirectionUrl;

    /**
     * The User service.
     */
    @Autowired
    UserService userService;

    /**
     * The Jwt service.
     */
    @Autowired
    JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();
        String email = attributes.get("email").toString();
        String name = attributes.get("name").toString();

        userService.getUserByEmail(email).ifPresentOrElse(
            (user) -> {
                if(user.isActive()){
                    String jwtToken = jwtService.generateToken(user);

                    if(user.getName().equals(name)){
                        user.setName(name);
                        userService.updateUser(user);
                    }

                    // Add cookie to the response
                    ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
                            .httpOnly(false)
                            .secure(false)
                            .path("/")
                            .maxAge(jwtService.getExpirationTime())
                            .sameSite("Strict")
                            .build();

                    response.addHeader("Set-Cookie", jwtCookie.toString());

                }else {
                    try {
                        response.sendRedirect("/error?message=Your account is deactivated.");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            },
            () -> {
                try {
                    response.sendRedirect("/error?message=Not authorized to create account.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        );

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(redirectionUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

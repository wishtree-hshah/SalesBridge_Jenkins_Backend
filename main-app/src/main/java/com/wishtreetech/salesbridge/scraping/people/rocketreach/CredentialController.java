package com.wishtreetech.salesbridge.scraping.people.rocketreach;

import com.wishtreetech.commonutils.dto.ResponseDto;
import com.wishtreetech.rocketreach.login.entity.Credential;
import com.wishtreetech.rocketreach.login.service.BrowserService;
import com.wishtreetech.rocketreach.login.service.CookieService;
import com.wishtreetech.rocketreach.login.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Credential controller.
 */
@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private BrowserService browserService;

    /**
     * Update or add a credential.
     *
     * @param credential the credential
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('MANAGE_ROCKETREACH_CREDENTIAL')")
    @PostMapping
    public ResponseEntity<ResponseDto<String>> updateCredentials(@RequestBody Credential credential) {
        if (credential == null || credential.getEmail() == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Credential or email cannot be null");
        }

        try {
            credential = credentialService.updateCredential(credential);
            cookieService.deleteAllCookies(credential);
            browserService.refreshCredentials();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDto<>(true, "Credential updated successfully"));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Get all credentials.
     *
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('MANAGE_ROCKETREACH_CREDENTIAL')")
    @GetMapping
    public ResponseEntity<ResponseDto<List<Credential>>> getCredentials() {
        List<Credential> credentials = credentialService.getAllCredentials();
        return ResponseEntity.ok(new ResponseDto<>(true, credentials));
    }

    /**
     * Delete a credential.
     *
     * @param credential the credential
     * @return the response entity
     */
    @PreAuthorize("hasAuthority('MANAGE_ROCKETREACH_CREDENTIAL')")
    @DeleteMapping
    public ResponseEntity<ResponseDto<String>> deleteCredential(@RequestBody Credential credential) {
        if (credential == null || credential.getEmail() == null) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Credential or email cannot be null");
        }

        try {
            Credential existingCredential = credentialService.getCredentialByEmail(credential.getEmail());
            if (existingCredential == null) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, "Credential not found");
            }

            credentialService.deleteCredential(existingCredential.getId());
            return ResponseEntity.ok(new ResponseDto<>(true, "Credential deleted successfully"));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Generic method to build an error response.
     *
     * @param status  HTTP status code
     * @param message Error message
     * @return ResponseEntity with ResponseDto<String>
     */
    private ResponseEntity<ResponseDto<String>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ResponseDto<>(false, message));
    }
}

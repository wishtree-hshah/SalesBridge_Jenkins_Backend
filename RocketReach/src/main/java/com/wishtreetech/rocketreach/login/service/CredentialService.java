package com.wishtreetech.rocketreach.login.service;

import com.wishtreetech.rocketreach.login.entity.Credential;

import java.util.List;

/**
 * The interface Credential service.
 */
public interface CredentialService {

    /**
     * Updates the credential in the database if already available, else creates a new one.
     * @param credential the credential object to update or create
     */
    Credential updateCredential(Credential credential);

    /**
     * Gets the credential by email.
     * @param email the email address for which the credential should be retrieved
     * @return the Credential object if found, null otherwise
     */
    Credential getCredentialByEmail(String email);

    /**
     * Gets the credential by id.
     * @param id
     * @return
     */
    Credential getCredentialById(Long id);

    List<Credential> getAllCredentials();

    void deleteCredential(Long id);
}

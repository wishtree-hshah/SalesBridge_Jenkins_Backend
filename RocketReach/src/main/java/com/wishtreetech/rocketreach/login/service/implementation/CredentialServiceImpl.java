package com.wishtreetech.rocketreach.login.service.implementation;

import com.wishtreetech.rocketreach.login.entity.Credential;
import com.wishtreetech.rocketreach.login.repository.CredentialRepository;
import com.wishtreetech.rocketreach.login.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The type Credential service implementation.
 */
@Service
public class CredentialServiceImpl implements CredentialService {

    private final CredentialRepository credentialRepository;

    /**
     * Instantiates a new Credential service.
     *
     * @param credentialRepository the credential repository
     */
    @Autowired
    public CredentialServiceImpl(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    /**
     * Updates the credential in the database if already available, else creates a new one.
     * @param credential the credential object to update or create
     */
    @Override
    public Credential updateCredential(Credential credential) {
        credentialRepository.findByEmail(credential.getEmail()).ifPresent(existingCredential -> {
            credential.setId(existingCredential.getId());
        });
        return credentialRepository.save(credential);
    }

    /**
     * Gets the credential by email.
     * @param email the email address for which the credential should be retrieved
     * @return the Credential object if found, null otherwise
     */
    @Override
    public Credential getCredentialByEmail(String email) {
        return credentialRepository.findByEmail(email).orElse(null);
    }

    /**
     * Gets the credential by id.
     *
     * @param id
     * @return
     */
    @Override
    public Credential getCredentialById(Long id) {
        return credentialRepository.findById(id).orElse(null);
    }

    /**
     * Gets all credentials.
     *
     * @return
     */
    @Override
    public List<Credential> getAllCredentials() {
        return credentialRepository.findAll();
    }

    /**
     * Deletes a credential by id.
     *
     * @param id
     */
    @Override
    public void deleteCredential(Long id) {
        credentialRepository.deleteById(id);
    }

}

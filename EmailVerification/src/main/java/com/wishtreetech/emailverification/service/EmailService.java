package com.wishtreetech.emailverification.service;

import com.wishtreetech.emailverification.entity.Person;

import java.util.List;

/**
 * Service interface for email verification operations.
 */
public interface EmailService {

    /**
     * Generates possible email patterns based on a person's full name and domain.
     *
     * @param fullName The full name of the person (e.g., "John Doe").
     * @param domain   The domain of the email address (e.g., "example.com").
     * @return A list of generated email patterns.
     */
    List<String> generateEmailPatterns(String fullName, String domain);

    /**
     * Validates an email address by checking its format and performing an SMTP handshake.
     *
     * @param email The email address to validate.
     * @return {@code true} if the email is valid, {@code false} otherwise.
     */
    boolean validateEmail(String email);

    /**
     * Generates and verifies email addresses based on the person's full name and domain.
     *
     * @param fullName The full name of the person.
     * @param domain   The domain of the email address.
     * @return A list of verified email addresses.
     */
    List<String> getVerifiedEmails(String fullName, String domain);

    /**
     * Concurrently verifies multiple email addresses for a list of persons.
     *
     * @param people A list of {@link Person} objects containing names and domains.
     * @return A list of {@link Person} objects with verified emails.
     */
    List<Person> getVerifiedEmailsConcurrent(List<Person> people);

    /**
     * Verifies a single person's email addresses.
     *
     * @param person A {@link Person} object containing the name and domain.
     * @return A {@link Person} object with verified emails.
     */
    Person getVerifiedEmail(Person person);
}

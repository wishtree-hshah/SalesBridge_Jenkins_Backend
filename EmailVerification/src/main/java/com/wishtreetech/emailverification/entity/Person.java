package com.wishtreetech.emailverification.entity;

import java.util.List;

/**
 * Represents a person with a name, domain, and a list of associated emails.
 * Also includes a warning message in case of verification issues.
 */
public class Person {
    private String name;
    private String domain;
    private List<String> emails;
    private String warning;

    /**
     * Gets the warning message related to email verification.
     *
     * @return the warning message.
     */
    public String getWarning() {
        return warning;
    }

    /**
     * Sets the warning message related to email verification.
     *
     * @param warning the warning message to set.
     */
    public void setWarning(String warning) {
        this.warning = warning;
    }

    /**
     * Constructs a new Person object with the specified name, domain, emails, and warning message.
     *
     * @param name    the full name of the person.
     * @param domain  the domain associated with the person's email.
     * @param emails  a list of verified email addresses.
     * @param warning a warning message if any issues occurred during verification.
     */
    public Person(String name, String domain, List<String> emails, String warning) {
        this.name = name;
        this.domain = domain;
        this.emails = emails;
        this.warning = warning;
    }

    /**
     * Default constructor for the Person class.
     */
    public Person() {
    }

    /**
     * Gets the name of the person.
     *
     * @return the person's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the person.
     *
     * @param name the person's name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the domain associated with the person's email.
     *
     * @return the email domain.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the domain associated with the person's email.
     *
     * @param domain the email domain to set.
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Gets the list of verified email addresses for the person.
     *
     * @return the list of verified emails.
     */
    public List<String> getEmails() {
        return emails;
    }

    /**
     * Sets the list of verified email addresses for the person.
     *
     * @param emails the list of emails to set.
     */
    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}

package com.wishtreetech.clutch.entity;

import java.util.List;

/**
 * The type Company details.
 */
public class CompanyDetails {
    private static int counter = 1;
    private int id;
    private String type;
    private String companyName;
    private String companySize;
    private String websiteLink;
    private String location;
    private String rating;
    private String minProjectSize;
    private String hourlyRate;
    private List<String> servicesProvided;
    private String profileUrl;
    private String about;
    private String verified;
    private String domain;
    private String companyLogo;

    /**
     * Instantiates a new Company details.
     *
     * @param type             the type
     * @param companyName      the company name
     * @param companySize      the company size
     * @param websiteLink      the website link
     * @param location         the location
     * @param rating           the rating
     * @param minProjectSize   the min project size
     * @param hourlyRate       the hourly rate
     * @param servicesProvided the services provided
     * @param profileUrl       the profile url
     * @param about            the about
     * @param verified         the verified
     * @param domain           the domain
     * @param companyLogo      the company logo
     */
    public CompanyDetails(String type, String companyName, String companySize, String websiteLink, String location, String rating, String minProjectSize, String hourlyRate, List<String> servicesProvided, String profileUrl, String about, String verified, String domain, String companyLogo) {
        this.id = counter++;
        this.type = type;
        this.companyName = companyName;
        this.companySize = companySize;
        this.websiteLink = websiteLink;
        this.location = location;
        this.rating = rating;
        this.minProjectSize = minProjectSize;
        this.hourlyRate = hourlyRate;
        this.servicesProvided = servicesProvided;
        this.profileUrl = profileUrl;
        this.about = about;
        this.verified = verified;
        this.domain = domain;
        this.companyLogo = companyLogo;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets company logo.
     *
     * @return the company logo
     */
    public String getCompanyLogo() {
        return companyLogo;
    }

    /**
     * Sets company logo.
     *
     * @param companyLogo the company logo
     */
    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    /**
     * Gets domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets domain.
     *
     * @param domain the domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets company name.
     *
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets company name.
     *
     * @param companyName the company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Gets company size.
     *
     * @return the company size
     */
    public String getCompanySize() {
        return companySize;
    }

    /**
     * Sets company size.
     *
     * @param companySize the company size
     */
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    /**
     * Gets website link.
     *
     * @return the website link
     */
    public String getWebsiteLink() {
        return websiteLink;
    }

    /**
     * Sets website link.
     *
     * @param websiteLink the website link
     */
    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets rating.
     *
     * @return the rating
     */
    public String getRating() {
        return rating;
    }

    /**
     * Sets rating.
     *
     * @param rating the rating
     */
    public void setRating(String rating) {
        this.rating = rating;
    }

    /**
     * Gets min project size.
     *
     * @return the min project size
     */
    public String getMinProjectSize() {
        return minProjectSize;
    }

    /**
     * Sets min project size.
     *
     * @param minProjectSize the min project size
     */
    public void setMinProjectSize(String minProjectSize) {
        this.minProjectSize = minProjectSize;
    }

    /**
     * Gets hourly rate.
     *
     * @return the hourly rate
     */
    public String getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Sets hourly rate.
     *
     * @param hourlyRate the hourly rate
     */
    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    /**
     * Gets services provided.
     *
     * @return the services provided
     */
    public List<String> getServicesProvided() {
        return servicesProvided;
    }

    /**
     * Sets services provided.
     *
     * @param servicesProvided the services provided
     */
    public void setServicesProvided(List<String> servicesProvided) {
        this.servicesProvided = servicesProvided;
    }

    /**
     * Gets profile url.
     *
     * @return the profile url
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * Sets profile url.
     *
     * @param profileUrl the profile url
     */
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    /**
     * Gets about.
     *
     * @return the about
     */
    public String getAbout() {
        return about;
    }

    /**
     * Sets about.
     *
     * @param about the about
     */
    public void setAbout(String about) {
        this.about = about;
    }

    /**
     * Gets verified.
     *
     * @return the verified
     */
    public String getVerified() {
        return verified;
    }

    /**
     * Sets verified.
     *
     * @param verified the verified
     */
    public void setVerified(String verified) {
        this.verified = verified;
    }
}

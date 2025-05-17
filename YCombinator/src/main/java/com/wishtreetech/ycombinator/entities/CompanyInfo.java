package com.wishtreetech.ycombinator.entities;

import java.util.List;
import java.util.Map;

/**
 * The type Company info.
 */
public class CompanyInfo {

    private List<Job> job;
    private List<Founder> founder;
    private String companyName;
    private Map<String, String> socialMediaLinks;
    private int foundedYear;

    /**
     * Instantiates a new Company info.
     *
     * @param job              the job
     * @param founder          the founder
     * @param companyName      the company name
     * @param socialMediaLinks the social media links
     * @param foundedYear      the founded year
     */
    public CompanyInfo(List<Job> job, List<Founder> founder, String companyName, Map<String,String> socialMediaLinks, int foundedYear) {
        this.job = job;
        this.founder = founder;
        this.companyName = companyName;
        this.socialMediaLinks = socialMediaLinks;
        this.foundedYear = foundedYear;
    }

    /**
     * Gets job.
     *
     * @return the job
     */
    public List<Job> getJob() {
        return job;
    }

    /**
     * Gets social media links.
     *
     * @return the social media links
     */
    public Map<String, String> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    /**
     * Gets founded year.
     *
     * @return the founded year
     */
    public int getFoundedYear() {
        return foundedYear;
    }

    /**
     * Sets founded year.
     *
     * @param foundedYear the founded year
     */
    public void setFoundedYear(int foundedYear) {
        this.foundedYear = foundedYear;
    }

    /**
     * Sets social media links.
     *
     * @param socialMediaLinks the social media links
     */
    public void setSocialMediaLinks(Map<String, String> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    /**
     * Sets job.
     *
     * @param job the job
     */
    public void setJob(List<Job> job) {
        this.job = job;
    }

    /**
     * Gets founder.
     *
     * @return the founder
     */
    public List<Founder> getFounder() {
        return founder;
    }

    /**
     * Sets founder.
     *
     * @param founder the founder
     */
    public void setFounder(List<Founder> founder) {
        this.founder = founder;
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
}

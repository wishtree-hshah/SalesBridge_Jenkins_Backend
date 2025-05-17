package com.wishtreetech.ycombinator.entities;

/**
 * The type Job.
 */
public class Job {

    private String jobTitle;
    private String location;
    private String applyLink;

    /**
     * Instantiates a new Job.
     *
     * @param jobTitle  the job title
     * @param location  the location
     * @param applyLink the apply link
     */
    public Job(String jobTitle, String location, String applyLink) {
        this.jobTitle = jobTitle;
        this.location = location;
        this.applyLink = applyLink;
    }

    /**
     * Gets job title.
     *
     * @return the job title
     */
// Getters and Setters
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets job title.
     *
     * @param jobTitle the job title
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
     * Gets apply link.
     *
     * @return the apply link
     */
    public String getApplyLink() {
        return applyLink;
    }

    /**
     * Sets apply link.
     *
     * @param applyLink the apply link
     */
    public void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobTitle='" + jobTitle + '\'' +
                ", location='" + location + '\'' +
                ", applyLink='" + applyLink + '\'' +
                '}';
    }
}

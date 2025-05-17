package com.wishtreetech.ycombinator.entities;

/**
 * The type Founder.
 */
public class Founder {

    private String founderName;
    private String founderRole;
    private String linkedInProfile;
    private String twitterProfile;
    private String founderImage;

    /**
     * Instantiates a new Founder.
     *
     * @param founderName     the founder name
     * @param founderRole     the founder role
     * @param linkedInProfile the linked in profile
     * @param twitterProfile  the twitter profile
     * @param founderImage    the founder image
     */
    public Founder(String founderName, String founderRole, String linkedInProfile, String twitterProfile, String founderImage) {
        this.founderName = founderName;
        this.founderRole = founderRole;
        this.linkedInProfile = linkedInProfile;
        this.twitterProfile = twitterProfile;
        this.founderImage = founderImage;
    }

    /**
     * Gets founder image.
     *
     * @return the founder image
     */
    public String getFounderImage() {
        return founderImage;
    }

    /**
     * Sets founder image.
     *
     * @param founderImage the founder image
     */
    public void setFounderImage(String founderImage) {
        this.founderImage = founderImage;
    }

    /**
     * Gets founder name.
     *
     * @return the founder name
     */
    public String getFounderName() {
        return founderName;
    }

    /**
     * Sets founder name.
     *
     * @param founderName the founder name
     */
    public void setFounderName(String founderName) {
        this.founderName = founderName;
    }

    /**
     * Gets founder role.
     *
     * @return the founder role
     */
    public String getFounderRole() {
        return founderRole;
    }

    /**
     * Sets founder role.
     *
     * @param founderRole the founder role
     */
    public void setFounderRole(String founderRole) {
        this.founderRole = founderRole;
    }

    /**
     * Gets linked in profile.
     *
     * @return the linked in profile
     */
    public String getLinkedInProfile() {
        return linkedInProfile;
    }

    /**
     * Sets linked in profile.
     *
     * @param linkedInProfile the linked in profile
     */
    public void setLinkedInProfile(String linkedInProfile) {
        this.linkedInProfile = linkedInProfile;
    }

    /**
     * Gets twitter profile.
     *
     * @return the twitter profile
     */
    public String getTwitterProfile() {
        return twitterProfile;
    }

    /**
     * Sets twitter profile.
     *
     * @param twitterProfile the twitter profile
     */
    public void setTwitterProfile(String twitterProfile) {
        this.twitterProfile = twitterProfile;
    }
}

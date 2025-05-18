package com.wishtreetech.rocketreach.scrapping.dto;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Query params.
 */
public class PeopleQueryParams {
    private Integer start = 1;
    private Integer pageSize = 10;
    private String name;
    private List<String> geo;
    private List<String> currentTitle;
    private List<String> department;
    private List<String> managementLevels;
    private String jobChangeRangeDays;
    private List<String> skills;
    private String yearsExperience;
    private List<String> employer;
    private List<String> companySize;
    private List<String> companyRevenue;
    private List<String> companyIndustry;
    private List<String> keyword;

    /**
     * Gets start.
     *
     * @return the start
     */
    public Integer getStart() {
        return start;
    }

    /**
     * Sets start.
     *
     * @param start the start
     */
    public void setStart(Integer start) {
        this.start = start;
    }

    /**
     * Gets page size.
     *
     * @return the page size
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Sets page size.
     *
     * @param pageSize the page size
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets geo.
     *
     * @return the geo
     */
    public List<String> getGeo() {
        return geo;
    }

    /**
     * Sets geo.
     *
     * @param geo the geo
     */
    public void setGeo(List<String> geo) {
        this.geo = geo;
    }

    /**
     * Gets current title.
     *
     * @return the current title
     */
    public List<String> getCurrentTitle() {
        return currentTitle;
    }

    /**
     * Sets current title.
     *
     * @param currentTitle the current title
     */
    public void setCurrentTitle(List<String> currentTitle) {
        this.currentTitle = currentTitle;
    }

    /**
     * Gets department.
     *
     * @return the department
     */
    public List<String> getDepartment() {
        return department;
    }

    /**
     * Sets department.
     *
     * @param department the department
     */
    public void setDepartment(List<String> department) {
        this.department = department;
    }

    /**
     * Gets management levels.
     *
     * @return the management levels
     */
    public List<String> getManagementLevels() {
        return managementLevels;
    }

    /**
     * Sets management levels.
     *
     * @param managementLevels the management levels
     */
    public void setManagementLevels(List<String> managementLevels) {
        this.managementLevels = managementLevels;
    }

    /**
     * Gets job change range days.
     *
     * @return the job change range days
     */
    public String getJobChangeRangeDays() {
        return jobChangeRangeDays;
    }

    /**
     * Sets job change range days.
     *
     * @param jobChangeRangeDays the job change range days
     */
    public void setJobChangeRangeDays(String jobChangeRangeDays) {
        this.jobChangeRangeDays = jobChangeRangeDays;
    }

    /**
     * Gets skills.
     *
     * @return the skills
     */
    public List<String> getSkills() {
        return skills;
    }

    /**
     * Sets skills.
     *
     * @param skills the skills
     */
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    /**
     * Gets years experience.
     *
     * @return the years experience
     */
    public String getYearsExperience() {
        return yearsExperience;
    }

    /**
     * Sets years experience.
     *
     * @param yearsExperience the years experience
     */
    public void setYearsExperience(String yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    /**
     * Gets employer.
     *
     * @return the employer
     */
    public List<String> getEmployer() {
        return employer;
    }

    /**
     * Sets employer.
     *
     * @param employer the employer
     */
    public void setEmployer(List<String> employer) {
        this.employer = employer;
    }

    /**
     * Gets company size.
     *
     * @return the company size
     */
    public List<String> getCompanySize() {
        return companySize;
    }

    /**
     * Sets company size.
     *
     * @param companySize the company size
     */
    public void setCompanySize(List<String> companySize) {
        this.companySize = companySize;
    }

    /**
     * Gets company revenue.
     *
     * @return the company revenue
     */
    public List<String> getCompanyRevenue() {
        return companyRevenue;
    }

    /**
     * Sets company revenue.
     *
     * @param companyRevenue the company revenue
     */
    public void setCompanyRevenue(List<String> companyRevenue) {
        this.companyRevenue = companyRevenue;
    }

    /**
     * Gets company industry.
     *
     * @return the company industry
     */
    public List<String> getCompanyIndustry() {
        return companyIndustry;
    }

    /**
     * Sets company industry.
     *
     * @param companyIndustry the company industry
     */
    public void setCompanyIndustry(List<String> companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    /**
     * Gets keyword.
     *
     * @return the keyword
     */
    public List<String> getKeyword() {
        return keyword;
    }

    /**
     * Sets keyword.
     *
     * @param keyword the keyword
     */
    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        StringBuilder query = new StringBuilder("?");

        appendParam(query, "start", start);
        appendParam(query, "pageSize", pageSize);
        appendParam(query, "name", name);
        appendListParam(query, "geo%5B%5D", geo);
        appendListParam(query, "current_title%5B%5D", currentTitle);
        appendListParam(query, "department%5B%5D", department);
        appendListParam(query, "management_levels%5B%5D", managementLevels);
        appendParam(query, "job_change_range_days()", jobChangeRangeDays);
        appendListParam(query, "skills%5B%5D", skills);
        appendParam(query, "years_experience()", yearsExperience);
        appendExactParam(query, "employer%5B%5D", employer);
        appendListParam(query, "company_size()", companySize);
        appendListParam(query, "company_revenue()", companyRevenue);
        appendListParam(query, "company_industry%5B%5D", companyIndustry);
        appendListParam(query, "keyword%5B%5D", keyword);

        // Remove the last '&' if exists
        if (query.charAt(query.length() - 1) == '&') {
            query.deleteCharAt(query.length() - 1);
        }

        return query.toString();
    }

    private void appendParam(StringBuilder query, String key, Object value) {
        if (value != null) {
            query.append(key)
                    .append("=")
                    .append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8))
                    .append("&");
        }
    }

    private void appendExactParam(StringBuilder query, String key, List<String> values) {
        if (values != null && !values.isEmpty()) {
            String joinedValues = values.stream()
                    .map(value -> URLEncoder.encode(value, StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&" + key + "="));

            query.append(key)
                    .append("=")
                    .append("\"")
                    .append(joinedValues)
                    .append("\"")
                    .append("&");
        }
    }

    private void appendListParam(StringBuilder query, String key, List<String> values) {
        if (values != null && !values.isEmpty()) {
            String joinedValues = values.stream()
                    .map(value -> URLEncoder.encode(value, StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&" + key + "="));

            query.append(key)
                    .append("=")
                    .append(joinedValues)
                    .append("&");
        }
    }
}


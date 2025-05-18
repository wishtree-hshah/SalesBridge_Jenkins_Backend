package com.wishtreetech.commonutils.dto.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * DTO for export requests containing company data and recipient email
 */
public class ExportRequestDTO {

    @NotEmpty(message = "Companies list cannot be empty")
    private List<Map<String, Object>> companies;

    @JsonProperty("detailsMap")
    private Map<String, Object> detailsMap;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Company type is required")
    private String companyType;

    private String exportType;

    // Constructors
    public ExportRequestDTO() {
    }

    public ExportRequestDTO(List<Map<String, Object>> companies, Map<String, Object> detailsMap,
                            String email, String companyType, String exportType) {
        this.companies = companies;
        this.detailsMap = detailsMap;
        this.email = email;
        this.companyType = companyType;
        this.exportType = exportType;
    }

    // Getters and Setters
    public List<Map<String, Object>> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Map<String, Object>> companies) {
        this.companies = companies;
    }

    public Map<String, Object> getDetailsMap() {
        return detailsMap;
    }

    public void setDetailsMap(Map<String, Object> detailsMap) {
        this.detailsMap = detailsMap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
}
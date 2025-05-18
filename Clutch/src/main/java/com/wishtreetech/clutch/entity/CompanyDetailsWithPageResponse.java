package com.wishtreetech.clutch.entity;

public class CompanyDetailsWithPageResponse {
    private CompanyDetailsResponse companyDetails;
    private int pageNumber;

    public CompanyDetailsWithPageResponse(CompanyDetailsResponse companyDetails, int pageNumber) {
        this.companyDetails = companyDetails;
        this.pageNumber = pageNumber;
    }

    // Getters and setters
    public CompanyDetailsResponse getCompanyDetails() {
        return companyDetails;
    }

    public void setCompanyDetails(CompanyDetailsResponse companyDetails) {
        this.companyDetails = companyDetails;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}

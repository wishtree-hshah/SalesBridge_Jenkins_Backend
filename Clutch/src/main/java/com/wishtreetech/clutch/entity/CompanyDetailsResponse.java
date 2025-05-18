package com.wishtreetech.clutch.entity;

import java.util.List;

/**
 * The type Company details response.
 */
public class CompanyDetailsResponse {
    private List<CompanyDetails> providerList;
    private List<CompanyDetails> featuredList;

    /**
     * Instantiates a new Company details response.
     *
     * @param providerList the provider list
     * @param featuredList the featured list
     */
    public CompanyDetailsResponse(List<CompanyDetails> providerList, List<CompanyDetails> featuredList) {
        this.providerList = providerList;
        this.featuredList = featuredList;
    }

    /**
     * Gets provider list.
     *
     * @return the provider list
     */
    public List<CompanyDetails> getProviderList() {
        return providerList;
    }

    /**
     * Gets featured list.
     *
     * @return the featured list
     */
    public List<CompanyDetails> getFeaturedList() {
        return featuredList;
    }
}

package com.wishtreetech.commonutils.scraping;

import org.springframework.stereotype.Service;

/**
 * The interface Company scraping.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @param <V> the type parameter
 * @param <W> the type parameter
 * @param <X> the type parameter
 */
@Service
public interface CompanyScraping<T, U, V, W, X> {

    /**
     * Gets company details.
     *
     * @param parameters the parameters
     * @return the company details
     */
    T getCompanyDetails(W parameters);

    /**
     * Gets filters.
     *
     * @return the filters
     */
    U getFilters();

    /**
     * Apply filters t.
     *
     * @param filters the filters
     * @return the t
     */
    T applyFilters(W filters);

    /**
     * Gets company info.
     *
     * @param companyUrl the company url
     * @return the company info
     */
    default V getCompanyInfo(W companyUrl) {
        throw new UnsupportedOperationException("getCompanyInfo is not supported for this implementation");
    }

    /**
     * Gets page number.
     *
     * @param url the url
     * @return the page number
     */
    default X getPageNumber(W url) {
        throw new UnsupportedOperationException("getPageNumber is not supported for this implementation");
    }
}

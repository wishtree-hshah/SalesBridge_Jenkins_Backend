package com.wishtreetech.commonutils.scraping;

import org.springframework.stereotype.Service;

/**
 * Represents a generic interface for scraping person data based on specified filters.
 * This interface provides a method to retrieve person data that matches the given filters.
 *
 * @param <T> The type representing the output person data.
 * @param <F> The type representing the filter criteria used for searching persons.
 */
@Service
public interface PeopleScrapping<T, F> {

    /**
     * Retrieves persons' data that matches the specified filter criteria.
     *
     * @param filters The filter criteria used to search for persons.
     * @return The person data matching the provided filters.
     */
    T getPeople(F filters);
}
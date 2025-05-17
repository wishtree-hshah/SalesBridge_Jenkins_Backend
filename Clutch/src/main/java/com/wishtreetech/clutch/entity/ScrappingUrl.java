package com.wishtreetech.clutch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * The type Scrapping url.
 */
@Entity
public class ScrappingUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String url;

    /**
     * Instantiates a new Scrapping url.
     */
    public ScrappingUrl() {
    }

    private String label;

    /**
     * Instantiates a new Scrapping url.
     *
     * @param url   the url
     * @param label the label
     */
    public ScrappingUrl(String url, String label) {
        this.url = url;
        this.label = label;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label.
     *
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }
}

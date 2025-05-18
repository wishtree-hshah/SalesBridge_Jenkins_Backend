package com.wishtreetech.salesbridge.login.entity;

import jakarta.persistence.*;

/**
 * The type Authority.
 */
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, unique = true, nullable = false)
    private AuthorityEnum name;

    /**
     * Instantiates a new Authority.
     */
    public Authority() {
    }

    /**
     * Instantiates a new Authority.
     *
     * @param authority the authority
     */
    public Authority(AuthorityEnum authority) {
        this.name = authority;
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
     * Gets name.
     *
     * @return the name
     */
    public AuthorityEnum getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(AuthorityEnum name) {
        this.name = name;
    }

}

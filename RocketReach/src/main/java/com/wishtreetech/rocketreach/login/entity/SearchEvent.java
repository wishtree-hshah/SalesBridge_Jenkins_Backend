package com.wishtreetech.rocketreach.login.entity;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Store the event of search query with credentialId and timestamp
 */
@Entity
public class SearchEvent {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "credential_id")
    private Credential credential;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

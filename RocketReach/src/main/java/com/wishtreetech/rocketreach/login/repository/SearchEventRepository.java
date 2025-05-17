package com.wishtreetech.rocketreach.login.repository;

import com.wishtreetech.rocketreach.login.entity.Credential;
import com.wishtreetech.rocketreach.login.entity.SearchEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SearchEventRepository extends JpaRepository<SearchEvent, Long> {

    // Using a JPQL query to count the number of search queries in the last 24 hours for a specific Credential
    @Query("SELECT COUNT(se) FROM SearchEvent se WHERE se.timestamp >= :startDate AND se.credential = :credential")
    long countSearchQueriesInLast24Hours(Date startDate, Credential credential);
}

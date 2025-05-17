package com.wishtreetech.rocketreach.login.service;

import com.wishtreetech.rocketreach.login.entity.Credential;
import com.wishtreetech.rocketreach.login.entity.SearchEvent;
import org.springframework.stereotype.Service;

@Service
public interface SearchEventService {
    long countSearchQueriesInLast24Hours(Credential credential);
    void saveEvent(SearchEvent searchEvent);
    void deleteEvent(SearchEvent searchEvent);
}

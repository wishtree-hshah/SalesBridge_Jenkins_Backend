package com.wishtreetech.rocketreach.login.service.implementation;

import com.wishtreetech.rocketreach.login.entity.Credential;
import com.wishtreetech.rocketreach.login.entity.SearchEvent;
import com.wishtreetech.rocketreach.login.repository.SearchEventRepository;
import com.wishtreetech.rocketreach.login.service.SearchEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class SearchEventServiceImpl implements SearchEventService {

    @Autowired
    private SearchEventRepository searchEventRepository;

    @Override
    public long countSearchQueriesInLast24Hours(Credential credential) {
        Date currentDate = new Date();

        // Subtract 24 hours from the current date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.HOUR_OF_DAY, -24);

        Date startDate = calendar.getTime();

        // Call the repository method to count the search events in the last 24 hours
        return searchEventRepository.countSearchQueriesInLast24Hours(startDate, credential);
    }

    @Override
    public void saveEvent(SearchEvent searchEvent) {
        searchEventRepository.save(searchEvent);
    }

    @Override
    public void deleteEvent(SearchEvent searchEvent) {
        searchEventRepository.delete(searchEvent);
    }
}
package com.wishtreetech.salesbridge.scraping.people.rocketreach;

import com.wishtreetech.commonutils.dto.ResponseDto;
import com.wishtreetech.rocketreach.scrapping.dto.PeopleQueryParams;
import com.wishtreetech.commonutils.scraping.PeopleScrapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rocketreach")
public class PeopleController {

    private final PeopleScrapping<ResponseDto, PeopleQueryParams> peopleService;

    @Autowired
    public PeopleController(@Qualifier("rocketReachPeopleService") PeopleScrapping<ResponseDto, PeopleQueryParams> peopleService) {
        this.peopleService = peopleService;
    }

    /**
     * Scrapes people data based on query parameters.
     *
     * @param queryParams the query parameters for scraping
     * @return ResponseEntity with ResponseDto containing scraping result
     */
    @PostMapping("/scrape")
    @PreAuthorize("hasAuthority('ROCKETREACH_ACCESS')")
    public ResponseEntity<ResponseDto> scrapePeople(@RequestBody PeopleQueryParams queryParams) {
        if (queryParams == null) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(false, "Query parameters cannot be null"));
        }

        try {
            System.out.println(queryParams);
            ResponseDto response = peopleService.getPeople(queryParams);
            System.out.println(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto<>(false, "Error occurred while scraping: " + e.getMessage()));
        }
    }
}

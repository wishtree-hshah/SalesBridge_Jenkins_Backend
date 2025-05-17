package com.wishtreetech.salesbridge.scraping.company.clutch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wishtreetech.clutch.entity.CompanyDetailsResponse;
import com.wishtreetech.clutch.entity.CompanyDetailsWithPageResponse;
import com.wishtreetech.clutch.entity.ScrappingUrl;
import com.wishtreetech.clutch.service.ScrappingUrlService;
import com.wishtreetech.commonutils.dto.ResponseDto;
import com.wishtreetech.commonutils.scraping.CompanyScraping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Clutch controller.
 */
@RestController
@RequestMapping("/api/clutch")
public class ClutchController {
    private final CompanyScraping<CompanyDetailsResponse, Map<String, Map<String, Object>>, String, String, Integer> companyScraping;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScrappingUrlService scrappingUrlService;

    private String URL = null;

    private String MAIN_URL = null;

    /**
     * Instantiates a new Clutch controller.
     *
     * @param companyScraping     the company scraping
     * @param scrappingUrlService the scrapping url service
     */
    @Autowired
    public ClutchController(CompanyScraping<CompanyDetailsResponse, Map<String, Map<String, Object>>, String, String, Integer> companyScraping, ScrappingUrlService scrappingUrlService) {
        this.companyScraping = companyScraping;
        this.scrappingUrlService = scrappingUrlService;
    }

    /**
     * Get page number response entity.
     *
     * @param url the url
     * @return the response entity
     */
    @PostMapping("/pageNumber")
    public ResponseEntity<ResponseDto> getPageNumber(@RequestBody Map<String, String> url){
        URL = url.get("url");
        MAIN_URL = url.get("url");
        return ResponseEntity.ok(new ResponseDto<>(true, companyScraping.getPageNumber(MAIN_URL)));
    }

    /**
     * Gets company details.
     *
     * @param request the request
     * @return the company details
     */
    @PostMapping
    public ResponseEntity<ResponseDto> getCompanyDetails(@RequestBody(required = false) Map<String, String> request) {
        String pageNumber = request.getOrDefault("pageNumber", "").trim();
        String mainUrl = MAIN_URL;

        if (!pageNumber.isEmpty()) {
            mainUrl += mainUrl.contains("?") ? "&page=" + pageNumber : "?page=" + pageNumber;
        }

        return ResponseEntity.ok(new ResponseDto<>(true, companyScraping.getCompanyDetails(mainUrl)));
    }

    /**
     * Gets all filters.
     *
     * @return the all filters
     */
    @GetMapping("/filters")
    public ResponseEntity<ResponseDto> getAllFilters() {
        return ResponseEntity.ok(new ResponseDto<>(true, companyScraping.getFilters()));
    }

    /**
     * Get page number for filter response entity.
     *
     * @param url the url
     * @return the response entity
     */
    public ResponseEntity<ResponseDto> getPageNumberForFilter(Map<String, String> url){
        MAIN_URL = url.get("url");
        return ResponseEntity.ok(new ResponseDto<>(true, companyScraping.getPageNumber(MAIN_URL)));
    }

    /**
     * Apply filters response entity.
     *
     * @param filters the filters
     * @return the response entity
     */
    @PostMapping("/apply/filters")
    public ResponseEntity<ResponseDto> applyFilters(@RequestBody String filters) {
        try {
            System.out.println(filters);

            // Decode the URL-encoded string and parse the JSON to a Map
            String decodedFilters = java.net.URLDecoder.decode(filters, "UTF-8");
            Map<String, Object> filtersMap = objectMapper.readValue(decodedFilters, Map.class);
            System.out.println("Parsed Filters: " + filtersMap);

            // Build the query string from the parsed filters
            StringBuilder queryString = new StringBuilder();

            filtersMap.forEach((key, value) -> {
                if (value instanceof List) {
                    for (Object val : (List<?>) value) {
                        appendQueryParam(queryString, key, val.toString());
                    }
                } else {
                    appendQueryParam(queryString, key, value.toString());
                }
            });

            // Construct final URL
            String finalUrl = URL + "?" + queryString;
            System.out.println("Final URL: " + finalUrl);
            MAIN_URL = finalUrl;

            // Fetch company details
            CompanyDetailsResponse response = companyScraping.getCompanyDetails(MAIN_URL);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDto<>(false, "Failed to fetch company details."));
            }

            Map<String, String> request = new HashMap<>();
            request.put("url", MAIN_URL);

            // Fetch total page count
            ResponseEntity<ResponseDto> totalPagesResponse = getPageNumberForFilter(request);
            Integer totalPageCount = null;

            if (totalPagesResponse != null) {
                ResponseDto<?> responseBody = totalPagesResponse.getBody();
                if (responseBody != null && responseBody.getData() instanceof Integer) {
                    totalPageCount = (Integer) responseBody.getData();
                }
            }

            if (totalPageCount == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDto<>(false, "Error processing filters: Total pages response is null."));
            }

            // Combine responses
            CompanyDetailsWithPageResponse combinedResponse = new CompanyDetailsWithPageResponse(response, totalPageCount);
            return ResponseEntity.ok(new ResponseDto<>(true, combinedResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto<>(false, "Error processing filters: " + e.getMessage()));
        }
    }

    /**
     * Helper method to append query parameters to a StringBuilder.
     */
    private void appendQueryParam(StringBuilder queryString, String key, String value) {
        if (queryString.length() > 0) {
            queryString.append("&");
        }
        queryString.append(key).append("=").append(value.replace(" ", "+"));
    }


    /**
     * Add url response entity.
     *
     * @param scrappingUrl the scrapping url
     * @return the response entity
     */
    @PostMapping("/url")
    public ResponseEntity<ResponseDto> addUrl(@RequestBody ScrappingUrl scrappingUrl) {
        System.out.println(scrappingUrl);
        try {
            System.out.println(scrappingUrl);
            scrappingUrlService.addUrl(scrappingUrl);
            return ResponseEntity.ok(new ResponseDto<>(true, "URL added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDto<>(false, "Adding URL failed: " + e.getMessage()));
        }
    }

    /**
     * Gets all urls.
     *
     * @return the all urls
     */
    @GetMapping("/url")
    public ResponseEntity<ResponseDto> getAllUrls() {
        try {
            return ResponseEntity.ok(new ResponseDto<>(true, scrappingUrlService.getAllUrls()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDto<>(false, "Fetching URLs failed: " + e.getMessage()));
        }
    }

    /**
     * Delete url response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @DeleteMapping("/url/{id}")
    public ResponseEntity<ResponseDto> deleteUrl(@PathVariable long id) {
        try {
            scrappingUrlService.deleteUrl(id);
            return ResponseEntity.ok(new ResponseDto<>(true, "URL deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDto<>(false, "Deleting URL failed: " + e.getMessage()));
        }
    }
}

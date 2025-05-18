package com.wishtreetech.salesbridge.scraping.company.ycombinator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wishtreetech.commonutils.dto.ResponseDto;
import com.wishtreetech.commonutils.scraping.CompanyScraping;
import com.wishtreetech.ycombinator.entities.CompanyInfo;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * The type Company controller.
 */
@RestController
@RequestMapping("/api/ycombinator")
public class YCombinatorController {
    private final CompanyScraping<String,String, CompanyInfo,String,Integer> companyScraping;
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new Company controller.
     *
     * @param companyScraping the company scraping
     * @param objectMapper    the object mapper
     */
    @Autowired
    public YCombinatorController(CompanyScraping<String, String, CompanyInfo, String, Integer> companyScraping, ObjectMapper objectMapper) {
        this.companyScraping = companyScraping;
        this.objectMapper = objectMapper;
    }

    /**
     * Gets all companies data.
     *
     * @param request the request
     * @return the companies
     */
    @PostMapping
    public ResponseEntity<ResponseDto> getCompanies(@RequestBody String request) {
        try {
            System.out.println(request);
            // Call service to fetch companies
            String response = companyScraping.getCompanyDetails(request);

            // Convert String response to JSON Object
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);

            // Wrap it in ResponseDto
            ResponseDto responseDto = new ResponseDto<>(true, jsonResponse);
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {// Return structured error response
            ResponseDto errorResponse = new ResponseDto<>(false, "Error fetching companies");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Gets all filters.
     *
     * @return the response entity
     */
    @GetMapping("/filter")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllFilters() {
        try {
            String response = companyScraping.getFilters();

            // Convert JSON string to Map
            Map<String, Object> jsonResponse = objectMapper.readValue(response, Map.class);

            // Wrap it inside ResponseDto
            ResponseDto responseDto = new ResponseDto<>(true, jsonResponse);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto<>(false, Map.of("error", "Failed to fetch API data")));
        }
    }

    /**
     * Gets company info.
     *
     * @param url the url
     * @return the company info
     */
    @GetMapping("/info")
    public ResponseEntity<ResponseDto> getCompanyInfo(@RequestParam String url) {
        try {
            // Fetch company info from the scraping service
            Object companyInfo = companyScraping.getCompanyInfo(url);

            // Wrap response in a standardized ResponseDto
            ResponseDto responseDto = new ResponseDto<>(true, companyInfo);

            return ResponseEntity.ok(responseDto); // Return HTTP 200 OK with response data
        } catch (Exception e) {
            ResponseDto<String> errorResponse = new ResponseDto<>(false, "Error fetching company info");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Apply filters response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping("/apply/filters")
    public ResponseEntity<ResponseDto> applyFilters(@RequestBody String request) {
        try {
            System.out.println(request);
            // Call service to fetch companies
            String response = companyScraping.applyFilters(request);

            // Convert String response to JSON Object
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);

            // Wrap it in ResponseDto
            ResponseDto responseDto = new ResponseDto<>(true, jsonResponse);
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {

            // Return structured error response
            ResponseDto errorResponse = new ResponseDto<>(false, "Error fetching companies");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}

package com.wishtreetech.ycombinator.service;


import com.wishtreetech.commonutils.scraping.CompanyScraping;
import com.wishtreetech.commonutils.service.WebDriverProvider;
import com.wishtreetech.ycombinator.entities.CompanyInfo;
import com.wishtreetech.ycombinator.entities.Founder;
import com.wishtreetech.ycombinator.entities.Job;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;

import java.util.*;

/**
 * The type Y combinator scrapping.
 */
@Service
public class YCombinatorScrappingImpl implements CompanyScraping<String,String, CompanyInfo,String,Integer> {
    @Value("${algolia.api.key}")
    private String algoliaApiKey;

    @Value("${algolia.application.id}")
    private String algoliaAppId;

    @Value("${algolia.api.url}")
    private String algoliaApiUrl;

    @Value("${algolia.api.url.full}")
    private String algoliaFullApiUrl;

    @Value("${algolia.request.body}")
    private String requestBody;

    private final RestTemplate restTemplate;
    private final WebDriverProvider webDriverProvider;
    /**
     * The Driver.
     */
    WebDriver driver = null;

    /**
     * Instantiates a new Y combinator scrapping.
     *
     * @param restTemplate      the rest template
     * @param webDriverProvider the web driver provider
     */
    public YCombinatorScrappingImpl(RestTemplate restTemplate, WebDriverProvider webDriverProvider) {
        this.restTemplate = restTemplate;
        this.webDriverProvider = webDriverProvider;
    }
    @Override
    public String getCompanyDetails(String parameters) {
        try {
            // Prepare the request headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");
            headers.set("x-algolia-agent", "Algolia%20for%20JavaScript%20(3.35.1)%3B%20Browser%3B%20JS%20Helper%20(3.16.1)");
            headers.set("x-algolia-application-id", algoliaAppId);
            headers.set("x-algolia-api-key", algoliaApiKey);

            // Prepare the request body
            String requestBody = "{ \"requests\": [ { \"indexName\": \"YCCompany_production\", \"params\": \""
                    + parameters + "\" } ] }";

            // Prepare the HTTP entity with headers and body
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // Create a RestTemplate to send the request
            RestTemplate restTemplate = new RestTemplate();

            // Send the request to Algolia
            ResponseEntity<String> response = restTemplate.exchange(
                    algoliaApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching company details: " + e.getMessage());
        }
    }

    @Override
    public String getFilters() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(algoliaFullApiUrl, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    @Override
    public String applyFilters(String filters) {
        return getCompanyDetails(filters);
    }

    @Override
    public CompanyInfo getCompanyInfo(String companyUrl) {
        driver = webDriverProvider.getWebDriver();

        try {
            driver.get(companyUrl);
            // Wait for a specific element to be present before continuing

            // Parse the page source with Jsoup
            Document document = Jsoup.parse(driver.getPageSource());

            // Extract data from the document
            String companyName = extractCompanyName(document);
            List<Job> job = extractJobDetails(companyUrl);
            List<Founder> founder = extractFounderDetails(document);
            Map<String, String> socialMediaLinks = extractSocialMediaLinks(document);
            int foundedYear = extractFoundedYear(document);

            // Create and return a CompanyInfo object
            return new CompanyInfo(job, founder, companyName, socialMediaLinks, foundedYear);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching company info: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private String extractCompanyName(Document document) {
        Element companyNameElement = document.selectFirst("div.flex.items-center.gap-x-3 h1");
        return companyNameElement != null ? companyNameElement.text() : "N/A";
    }

    private List<Job> extractJobDetails(String url) {
        List<Job> jobList = new ArrayList<>();
        WebDriver driver1 = webDriverProvider.getWebDriver();
        driver1.get(url.concat("/jobs"));
        // Wait for a specific element to be present before continuing

        // Parse the page source with Jsoup
        Document document = Jsoup.parse(driver1.getPageSource());

        // Select all job listings
        Elements jobElements = document.select("div.flex.w-full.flex-row.justify-between.border-b.py-4");

        for (Element jobElement : jobElements) {
            // Extract job title
            Element jobTitleElement = jobElement.selectFirst("div.ycdc-with-link-color.text-md.pr-4.font-medium a");
            String jobTitle = jobTitleElement != null ? jobTitleElement.text() : "N/A";

            // Extract job location
            Element locationElement = jobElement.selectFirst("div.capitalize");
            String jobLocation = locationElement != null ? locationElement.text() : "N/A";

            // Extract apply link
            Element applyLinkElement = jobElement.selectFirst("div.APPLY.flex.items-center a");
            String applyLink = applyLinkElement != null ? applyLinkElement.attr("href") : "N/A";

            // Add job to the list
            jobList.add(new Job(jobTitle, jobLocation, applyLink));
        }
        driver1.quit();

        return jobList;
    }
    private List<Founder> extractFounderDetails(Document document) {
        List<Founder> founders = new ArrayList<>();

        // Select each founder card
        Elements founderCards = document.select("div.ycdc-card-new.w-full");

        for (Element card : founderCards) {
            // Extract image
            String imageUrl = card.selectFirst("img") != null
                    ? card.selectFirst("img").attr("src")
                    : "N/A";

            // Extract name
            String name = card.selectFirst("div.text-xl.font-bold") != null
                    ? card.selectFirst("div.text-xl.font-bold").text().trim()
                    : "N/A";

            // Extract role
            String role = card.selectFirst("div.pt-1.text-[15px]") != null
                    ? card.selectFirst("div.pt-1").text().trim()
                    : "N/A";

            System.out.println("role");

            // Extract LinkedIn and Twitter (X) links
            String linkedIn = "N/A";
            String twitter = "N/A";

            Elements socialLinks = card.select("div.flex.gap-2 a");
            for (Element social : socialLinks) {
                String href = social.attr("href");
                String label = social.attr("aria-label").toLowerCase();

                if (label.contains("linkedin")) {
                    linkedIn = href;
                } else if (label.contains("twitter") || label.contains("x")) {
                    twitter = href;
                }
            }

            // Add to list
            founders.add(new Founder(name, role, linkedIn, twitter, imageUrl));
        }

        return founders;
    }


    private int extractFoundedYear(Document document) {
        Element foundedElement = document.selectFirst("div.flex.flex-row.justify-between span:nth-child(2)");
        if (foundedElement != null) {
            try {
                return Integer.parseInt(foundedElement.text().trim());
            } catch (NumberFormatException e) {
                System.err.println("Error parsing founded year: " + e.getMessage());
            }
        }
        return -1; // Return -1 if not found
    }
    private Map<String, String> extractSocialMediaLinks(Document document) {
        Map<String, String> socialMediaLinks = new HashMap<>();

        Elements socialLinks = document.select("div.flex.flex-wrap.items-center.gap-3.pt-2 a");

        for (Element linkElement : socialLinks) {
            String url = linkElement.attr("href");
            String label = linkElement.attr("aria-label").toLowerCase();

            if (label.contains("linkedin")) {
                socialMediaLinks.put("LinkedIn", url);
            } else if (label.contains("twitter") || label.contains("x")) {
                socialMediaLinks.put("Twitter", url);
            } else if (label.contains("facebook")) {
                socialMediaLinks.put("Facebook", url);
            } else if (label.contains("crunchbase")) {
                socialMediaLinks.put("Crunchbase", url);
            }
        }


        return socialMediaLinks;
    }


}

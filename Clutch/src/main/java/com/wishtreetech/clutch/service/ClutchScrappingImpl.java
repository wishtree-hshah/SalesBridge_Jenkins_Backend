package com.wishtreetech.clutch.service;

import com.wishtreetech.clutch.entity.CompanyDetails;
import com.wishtreetech.clutch.entity.CompanyDetailsResponse;
import com.wishtreetech.commonutils.scraping.CompanyScraping;
import com.wishtreetech.commonutils.service.WebDriverProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Clutch scrapping.
 */
@Service
public class ClutchScrappingImpl implements CompanyScraping<CompanyDetailsResponse, Map<String, Map<String, Object>>, String, String, Integer> {
    private static final Pattern FILTER_PATTERN = Pattern.compile("\\s*\\(\\d+\\)");
    private final WebDriverProvider webDriverProvider;
    /**
     * The Driver.
     */
    WebDriver driver = null;

    /**
     * Instantiates a new Clutch scraping service.
     *
     * @param webDriverProvider the web driver provider
     */

    @Value("${clutch.api.filter.url}")
    private String FILTER_URL;

    @Autowired
    public ClutchScrappingImpl(WebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    @Override
    public CompanyDetailsResponse getCompanyDetails(String parameters) {
        List<CompanyDetails> companyDetailsList = new ArrayList<>();
        List<CompanyDetails> companyDetailsFeatured = new ArrayList<>();

        try {
            driver = webDriverProvider.getWebDriver();
            driver.get(parameters);
            System.out.println(parameters);

            Document document = Jsoup.parse(driver.getPageSource());
            Elements companyCards = document.select("ul.providers__list li.provider-list-item"); // Select each company container

            for (Element companyCard : companyCards) {
                Element companyLink = companyCard.selectFirst("a.provider__title-link.directory_profile");
                String companyName = companyLink != null ? companyLink.text() : "N/A";

                Element companySize = companyCard.selectFirst("div.provider__highlights-item.sg-tooltip-v2.employees-count");
                String size = companySize != null ? companySize.text() : "N/A";

                Element websiteLink = companyCard.selectFirst("a.website-link__item");
                String website = websiteLink != null ? websiteLink.attr("href") : "N/A";
                Pattern pattern = Pattern.compile("u=([^&]*)");
                Matcher matcher = pattern.matcher(website);
                String domain = "N/A";

                if (matcher.find()) {
                    String encodedUrl = matcher.group(1);
                    String decodedUrl = URLDecoder.decode(encodedUrl, "UTF-8");

                    domain = decodedUrl.split("/")[2];
                    domain = domain.replaceFirst("^www\\.", "");
                }
                Element location = companyCard.selectFirst("div.provider__highlights-item.sg-tooltip-v2.location");
                String companyLocation = location != null ? location.text() : "N/A";

                Element ratingElement = companyCard.selectFirst("span.sg-rating__number meta[itemprop=ratingValue]");
                String rating = ratingElement != null ? ratingElement.attr("content") : "N/A";

                Element minProjectSizeElement = companyCard.selectFirst("div.provider__highlights-item.sg-tooltip-v2.min-project-size");
                String minProjectSize = minProjectSizeElement != null ? minProjectSizeElement.text().trim() : "N/A";

                Element hourlyRateElement = companyCard.selectFirst("div.provider__highlights-item.sg-tooltip-v2.hourly-rate");
                String hourlyRate = hourlyRateElement != null ? hourlyRateElement.text().trim() : "N/A";

                Elements servicesElements = companyCard.select("div.provider__services-list-item");
                List<String> servicesProvided = new ArrayList<>();
                for (Element serviceElement : servicesElements) {
                    String service = serviceElement.text();  // Extract the service name
                    servicesProvided.add(service);  // Or store it in an array/list
                }

                Element profileUrlElement = companyCard.selectFirst("a.directory_profile");
                String profileUrl = profileUrlElement != null ? ("https://clutch.co").concat(profileUrlElement.attr("href")) : "N/A";

                Element aboutElement = companyCard.selectFirst("div.provider__project-highlight-text p");
                String about = aboutElement != null ? aboutElement.text() : "N/A";

                Element verifiedMarkElement = companyCard.selectFirst("span.provider__verified-mark");
                String verified = verifiedMarkElement != null ? verifiedMarkElement.ownText().trim() : "N/A";

                Element companyLogoElement = companyCard.selectFirst(".sg-provider-logotype-v2.provider__logotype.directory_profile img");
                String companyLogo = companyLogoElement != null ? companyLogoElement.attr("src") : "N/A";

                CompanyDetails details = new CompanyDetails("provider",companyName, size, website, companyLocation,rating,minProjectSize,hourlyRate,servicesProvided,profileUrl,about,verified, domain, companyLogo);
                companyDetailsList.add(details);
            }

            // featured
            Elements companyCardsFeatured = document.select("ul.providers__list li.featured-listings div.provider-list-item"); // Select each company container

            for (Element companyCard : companyCardsFeatured) {
                Element companyLink = companyCard.selectFirst("a.provider__title-link.ppc-website-link");
                String companyName = companyLink != null ? companyLink.text() : "N/A";

                Element companySize = companyCard.selectFirst("div.provider__highlights-item.sg-tooltip-v2.employees-count");
                String size = companySize != null ? companySize.text() : "N/A";

                Element websiteLink = companyCard.selectFirst("a.website-link__item");
                String website = websiteLink != null ? websiteLink.attr("href") : "N/A";
                String domain = "N/A";
                try {
                    // Extract the "u" parameter value
                    Pattern pattern = Pattern.compile("u=([^&]+)");
                    Matcher matcher = pattern.matcher(website);
                    if (matcher.find()) {
                        String encodedUrl = matcher.group(1);
                        String decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);

                        // Extract the domain from the decoded URL
                        URI uri = new URI(decodedUrl);
                        domain = uri.getHost().replace("www.", ""); // Remove "www."
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error extracting domain from URL: " + website);
                }
                Element location = companyCard.selectFirst("div.provider__highlights-item.sg-tooltip-v2.location");
                String companyLocation = location != null ? location.text() : "N/A";

                Element ratingElement = companyCard.selectFirst("span.sg-rating__number");
                String rating = ratingElement != null ? ratingElement.text().trim() : "N/A";

                Element minProjectSizeElement = companyCard.selectFirst("div.provider__highlights-item.sg-tooltip-v2.min-project-size");
                String minProjectSize = minProjectSizeElement != null ? minProjectSizeElement.text().trim() : "N/A";

                Element hourlyRateElement = companyCard.selectFirst("div.provider__highlights-item.sg-tooltip-v2.hourly-rate");
                String hourlyRate = hourlyRateElement != null ? hourlyRateElement.text().trim() : "N/A";

                Elements servicesElements = companyCard.select("div.provider__services-list-item");
                List<String> servicesProvided = new ArrayList<>();
                for (Element serviceElement : servicesElements) {
                    String service = serviceElement.text();  // Extract the service name
                    servicesProvided.add(service);  // Or store it in an array/list
                }

                Element profileUrlElement = companyCard.selectFirst("a.directory_profile");
                String profileUrl = profileUrlElement != null ? ("https://clutch.co").concat(profileUrlElement.attr("href")) : "N/A";

                Element aboutElement = companyCard.selectFirst("div.provider__project-highlight-text p");
                String about = aboutElement != null ? aboutElement.text() : "N/A";

                Element verifiedMarkElement = companyCard.selectFirst("span.provider__verified-mark");
                String verified = verifiedMarkElement != null ? verifiedMarkElement.ownText().trim() : "N/A";

                CompanyDetails details = new CompanyDetails("featured",companyName, size, website, companyLocation,rating,minProjectSize,hourlyRate,servicesProvided,profileUrl,about,verified, domain, null);
                companyDetailsFeatured.add(details);
            }
            driver.quit();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching company details: " + e.getMessage());
        } finally {
            if (driver != null) {
                System.out.println("Quitting WebDriver...");
                driver.quit();
            }
        }
        return new CompanyDetailsResponse(companyDetailsList, companyDetailsFeatured);
    }


    @Override
    public Map<String, Map<String, Object>> getFilters() {
        Map<String, Map<String, Object>> filters = new HashMap<>();
        driver = webDriverProvider.getWebDriver();

        try {

            driver.get(FILTER_URL);
            System.out.println("filter: " + FILTER_URL);
            String htmlContent = driver.getPageSource();
            Document document = Jsoup.parse(htmlContent);

            // Select all filter types
            Elements filterContainers = document.select("div.facets_fly__btn.facets_fly__wrapper");

            for (Element filterContainer : filterContainers) {
                // Extract filter type
                Element filterTypeElement = filterContainer.selectFirst("div.arrow_icon");
                String filterType = filterTypeElement != null ? filterTypeElement.text().trim() : null;

                // Extract and clean filter ID
                String filterId = filterContainer.attr("id").replaceAll("_button__fly$", "");

                if (filterType != null) {
                    // Extract container ID for the associated values
                    String containerId = filterContainer.attr("aria-controls");
                    Map<String, String> filterValues = new HashMap<>();

                    // Fetch filter values specific to this container
                    Elements filterValueElements = document.select("#" + containerId + " .facets_list__item label");
                    for (Element valueElement : filterValueElements) {
                        // Remove numbers in parentheses from filter names
                        String filterValueName = FILTER_PATTERN.matcher(valueElement.text()).replaceAll("").trim();
                        String filterValue = valueElement.selectFirst("input").attr("value");

                        if (filterValue != null && !filterValue.isEmpty()) {
                            filterValues.put(filterValueName, filterValue);
                        }
                    }

                    // Store the filter type, its values, and cleaned ID
                    if (!filterValues.isEmpty()) {
                        Map<String, Object> filterDetails = new HashMap<>();
                        filterDetails.put("id", filterId);
                        filterDetails.put("values", filterValues);
                        filters.put(filterType, filterDetails);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while fetching filters: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return filters;
    }

    @Override
    public CompanyDetailsResponse applyFilters(String filters) {
        return null;
    }

    @Override
    public Integer getPageNumber(String url) {
        driver = webDriverProvider.getWebDriver();
        driver.get(url);
        System.out.println("Fetching: " + url);

        Document document = Jsoup.parse(driver.getPageSource());
        Elements pageElements = document.select("a.sg-pagination-v2-page-number");

        int totalPages = pageElements.stream()
                .mapToInt(element -> Integer.parseInt(element.text().trim()))
                .max()
                .orElse(1); // Default to 1 if no pages found

        System.out.println("Total Pages: " + totalPages);
        driver.quit();
        return totalPages;
    }
}

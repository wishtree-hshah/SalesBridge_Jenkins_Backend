package com.wishtreetech.rocketreach.scrapping.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wishtreetech.rocketreach.exception.ApiResponseException;
import com.wishtreetech.rocketreach.login.service.BrowserService;
import com.wishtreetech.rocketreach.scrapping.dto.PeopleQueryParams;
import com.wishtreetech.commonutils.dto.ResponseDto;
import com.wishtreetech.commonutils.scraping.PeopleScrapping;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.DevToolsException;
import org.openqa.selenium.devtools.v115.network.Network;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service("rocketReachPeopleService")
public class PeopleScrappingImpl implements PeopleScrapping<ResponseDto, PeopleQueryParams> {

    @Value("${rocketreach.scrapping.responseUrl}")
    private String responseUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(PeopleScrappingImpl.class);
    private final BrowserService browserService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new {@code PeopleServiceImpl} instance.
     *
     * @param browserService The {@link BrowserService} that provides WebDriver instances.
     */
    @Autowired
    public PeopleScrappingImpl(BrowserService browserService) {
        this.browserService = browserService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retrieves persons' data that matches the specified filter criteria.
     *
     * @param filters The filter criteria used to search for persons.
     * @return The person data matching the provided filters.
     */
    @Override
    public ResponseDto getPeople(PeopleQueryParams filters) {
        WebDriver driver = browserService.getDriver();
        DevTools devTools = null;

        // Open a new tab
        ((JavascriptExecutor) driver).executeScript("window.open();");

        // Switch to the newly opened tab
        Set<String> windowHandles = driver.getWindowHandles();
        String[] handles = windowHandles.toArray(new String[0]);
        driver.switchTo().window(handles[handles.length - 1]);  // Switch to the newly opened tab

        try {
            devTools = ((org.openqa.selenium.chrome.ChromeDriver) driver).getDevTools();
            devTools.createSession();
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

            AtomicReference<Object> responseEntityRef = new AtomicReference<>();

            DevTools finalDevTools = devTools;
            devTools.addListener(Network.responseReceived(), response -> {
                int statusCode = response.getResponse().getStatus();
                try {
                    String url = response.getResponse().getUrl();

                    if (url.contains(responseUrl)) {
                        if (statusCode == 200) {
                            Network.GetResponseBodyResponse responseBody = finalDevTools.send(Network.getResponseBody(response.getRequestId()));
                            List<Map> peoples = (List<Map>) objectMapper.readValue(responseBody.getBody(), Map.class).get("people");
                            responseEntityRef.set(new ResponseDto<>(true, peoples));
                        } else {
                            LOGGER.error("API Error: {} | Status Code: {}", url, statusCode);
                            responseEntityRef.set(new ApiResponseException(statusCode, "API error"));
                        }
                    }
                } catch (DevToolsException devToolsException) {
                    LOGGER.error("API Error: {} | Status Code: {}", responseUrl, statusCode);
                    responseEntityRef.set(new ApiResponseException(statusCode, "API error"));
                } catch (Exception exception) {
                    LOGGER.error("Error parsing response: {}", exception.getMessage(), exception);
                    responseEntityRef.set(new ApiResponseException(statusCode, "Error parsing response"));
                }
            });

            String requestUrl = "https://rocketreach.co/person" + filters.toString();

            int maxRetries = 3;
            int retryCount = 0;
            long backoffDelay = 2000; // Initial delay of 2 seconds

            while (retryCount < maxRetries) {
                driver.get(requestUrl);

                try {
                    new WebDriverWait(driver, Duration.ofSeconds(10))
                            .until(d -> responseEntityRef.get() != null);
                } catch (TimeoutException timeoutException) {
                    LOGGER.warn("Timeout: No response received. Retrying {}/{}", retryCount + 1, maxRetries);
                }

                if (responseEntityRef.get() instanceof ApiResponseException) {
                    LOGGER.error("Attempt {} failed: {}", retryCount + 1, ((ApiResponseException) responseEntityRef.get()).getMessage());
                } else if (responseEntityRef.get() instanceof ResponseDto) {
                    return (ResponseDto) new ResponseDto<>(true, responseEntityRef.get()); // Success, return response
                }

                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(backoffDelay);
                        backoffDelay *= 2; // Increase delay (2s → 4s → 8s)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new ApiResponseException(500, "Retry interrupted");
                    }
                }
            }

            throw new ApiResponseException(500, "Retries exhausted, request failed.");
        } catch (RuntimeException exception) {
            throw exception;
        } finally {
            if (devTools != null) {
                try {
                    devTools.close();
                } catch (Exception e) {
                    LOGGER.warn("Error closing DevTools: {}", e.getMessage(), e);
                }
            }
            browserService.releaseDriver(driver);
        }
    }
}

package com.wishtreetech.rocketreach.login.service.implementation;

import com.wishtreetech.rocketreach.exception.ApiResponseException;
import com.wishtreetech.rocketreach.login.entity.Credential;
import com.wishtreetech.rocketreach.login.entity.SearchEvent;
import com.wishtreetech.rocketreach.login.service.BrowserService;
import com.wishtreetech.rocketreach.login.service.CredentialService;
import com.wishtreetech.rocketreach.login.service.LoginService;
import com.wishtreetech.rocketreach.login.service.SearchEventService;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Implementation of the BrowserService that manages Selenium WebDriver instances
 * for different credentials, ensuring proper authentication and tracking API usage.
 */
@Service
@Scope("singleton")
public class BrowserServiceImpl implements BrowserService {

    private final CredentialService credentialService;
    private final LoginService loginService;
    private final SearchEventService searchEventService;
    private final Map<Credential, WebDriver> activeBrowsers = new ConcurrentHashMap<>();
    private final Queue<Credential> credentialQueue = new ConcurrentLinkedQueue<>();

    @Value("${rocketreach.max_searches_per_day:20}")
    private int MAX_SEARCHES_PER_DAY;

    /**
     * Constructs the BrowserServiceImpl and initializes the credential queue.
     *
     * @param credentialService Service to fetch available credentials.
     * @param loginService      Service to handle login operations.
     * @param searchEventService Service to track search query events.
     */
    @Autowired
    public BrowserServiceImpl(CredentialService credentialService, LoginService loginService, SearchEventService searchEventService) {
        this.credentialService = credentialService;
        this.loginService = loginService;
        this.searchEventService = searchEventService;
        initializeCredentials();
    }

    /**
     * Loads all available credentials into the credential queue.
     */
    private void initializeCredentials() {
        List<Credential> credentials = credentialService.getAllCredentials();
        credentialQueue.addAll(credentials);
    }

    /**
     * Retrieves an available WebDriver instance.
     * Ensures that the credential has not exceeded the search query limit for the last 24 hours.
     *
     * @return A Selenium WebDriver instance with an authenticated session.
     * @throws RuntimeException If no valid credentials are available or all have exceeded the search limit.
     */
    @Override
    public synchronized WebDriver getDriver() {
        if (credentialQueue.isEmpty() && activeBrowsers.isEmpty()) {
            throw new ApiResponseException(HttpStatus.BAD_REQUEST.value(), "No credentials available for login into he rocketreach");
        }

        while (credentialQueue.isEmpty()) {
            try {
                wait(); // Wait until a browser becomes available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        WebDriver driver = null;
        int maxAttempts = credentialQueue.size(); // Limit iterations to the initial queue size
        int attempts = 0;

        while (!credentialQueue.isEmpty() && attempts < maxAttempts) {
            Credential credential = credentialQueue.poll();
            credentialQueue.add(credential);
            attempts++;

            long searchCount = searchEventService.countSearchQueriesInLast24Hours(credential);
            if (searchCount < MAX_SEARCHES_PER_DAY) {
                driver = initializeBrowser(credential);
                activeBrowsers.put(credential, driver);
                incrementApiRequest(credential);
                break;
            }
        }


        if (driver == null) {
            throw new ApiResponseException(HttpStatus.BAD_REQUEST.value(), "Search limit is exceeded");
        }

        return driver;
    }

    /**
     * Initializes a new WebDriver session and logs in using the provided credential.
     *
     * @param credential The credential to use for login.
     * @return A WebDriver instance with an authenticated session.
     * @throws RuntimeException If login fails.
     */
    private WebDriver initializeBrowser(Credential credential) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--allow-insecure-localhost");
        options.addArguments("--allow-running-insecure-content");

//        // Anti-detection measures
//        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
//        options.setExperimentalOption("useAutomationExtension", false);
//
//        // Mimic real user behavior
//        options.addArguments("--disable-extensions");
//        options.addArguments("--disable-infobars");
//        options.addArguments("--disable-notifications");
//        options.addArguments("--disable-popup-blocking");
//        options.addArguments("--disable-default-apps");
//        options.addArguments("--disable-web-security");
//        options.addArguments("--disable-translate");
//        options.addArguments("--disable-logging");
//        options.addArguments("--log-level=3");
//        options.addArguments("--output=/dev/null");

        WebDriver driver = new ChromeDriver(options);

        if (!loginService.login(driver, credential)) {
            driver.quit();
            throw new ApiResponseException(HttpStatus.BAD_REQUEST.value(), "Login failed for credential: " + credential.getEmail());
        }
        return driver;
    }

    /**
     * Releases a WebDriver instance and makes the associated credential available for reuse.
     *
     * @param driver The WebDriver instance to be released.
     */
    public synchronized void releaseDriver(WebDriver driver) {
        Credential credential = activeBrowsers.entrySet().stream()
                .filter(entry -> entry.getValue().equals(driver))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (credential != null) {
            credentialQueue.add(credential);
            activeBrowsers.remove(credential);
            driver.quit();
            notifyAll(); // Notify waiting threads
        }
    }

    @Override
    public void refreshCredentials() {
        initializeCredentials();
    }

    /**
     * Logs a new search event for tracking API request usage.
     *
     * @param credential The credential used for the search.
     */
    public synchronized void incrementApiRequest(Credential credential) {
        SearchEvent searchEvent = new SearchEvent();
        searchEvent.setCredential(credential);
        searchEvent.setTimestamp(new Date());
        searchEventService.saveEvent(searchEvent);
    }
}

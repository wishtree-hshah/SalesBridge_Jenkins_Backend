package com.wishtreetech.rocketreach.login.service;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

/**
 * The interface Browser service.
 */
@Service
public interface BrowserService {
    /**
     * Creates and returns a new WebDriver instance that is already logged in.
     *
     * @return the logged-in WebDriver instance.
     */
    WebDriver getDriver();

    void releaseDriver(WebDriver driver);

    void refreshCredentials();

}

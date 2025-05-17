package com.wishtreetech.commonutils.service;
import org.openqa.selenium.WebDriver;

/**
 * The interface Web driver provider.
 */
public interface WebDriverProvider {
    /**
     * Gets web driver.
     *
     * @return the web driver
     */
    WebDriver getWebDriver();
}


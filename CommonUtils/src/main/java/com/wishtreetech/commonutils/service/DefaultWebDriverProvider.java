package com.wishtreetech.commonutils.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * The type Default web driver provider.
 */
@Component
public class DefaultWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver getWebDriver() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new"); // New headless mode (Chrome 112+)
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        // Anti-detection measures
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // Mimic real user behavior
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-translate");
        options.addArguments("--disable-logging");
        options.addArguments("--log-level=3");
        options.addArguments("--output=/dev/null");

        // Set user agent (important!)
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        return new ChromeDriver(options);
    }
}

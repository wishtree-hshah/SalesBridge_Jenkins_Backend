package com.wishtreetech.rocketreach.login.service.implementation;

import com.wishtreetech.rocketreach.login.entity.Cookie;
import com.wishtreetech.rocketreach.login.entity.Credential;
import com.wishtreetech.rocketreach.login.service.CookieService;
import com.wishtreetech.rocketreach.login.service.CredentialService;
import com.wishtreetech.rocketreach.login.service.LoginService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Login service.
 */
@Service
public class LoginServiceImpl implements LoginService {

    private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final CredentialService credentialService;
    private final CookieService cookieService;

    @Value("${rocketreach.login.url}")
    private String loginUrl;

    @Autowired
    public LoginServiceImpl(CredentialService credentialService, CookieService cookieService) {
        this.credentialService = credentialService;
        this.cookieService = cookieService;
    }

    /**
     * Logs in the given WebDriver instance using a specific credential.
     *
     * @param driver the WebDriver instance.
     * @param credential the credential to log in with.
     * @return true if login is successful.
     */
    @Override
    public boolean login(WebDriver driver, Credential credential) {
        if (validateCookie(driver, credential) || loginWithCredentials(driver, credential)) {
            return true;
        }
        return false;
    }

    private boolean loginWithCredentials(WebDriver driver, Credential credential) {
        try {
            String email = credential.getEmail();
            String password = credential.getPassword();

            driver.get(loginUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_email")));
            emailField.sendKeys(email);

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id_password")));
            passwordField.sendKeys(password);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            loginButton.click();

            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

            System.out.println("Login successful!");
            updateCookiesToDatabase(driver, credential);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void updateCookiesToDatabase(WebDriver driver, Credential credential) {
        Set<org.openqa.selenium.Cookie> driverCookies = driver.manage().getCookies();

        // Use parallel execution to update cookies faster
        try(
                ExecutorService executor = Executors.newFixedThreadPool(5)
                ){
            for (org.openqa.selenium.Cookie driverCookie : driverCookies) {
                executor.execute(() -> {
                    Cookie cookieEntity = new Cookie();
                    cookieEntity.setName(driverCookie.getName());
                    cookieEntity.setValue(driverCookie.getValue());
                    cookieEntity.setDomain(driverCookie.getDomain());
                    cookieEntity.setPath(driverCookie.getPath());
                    cookieEntity.setExpiry(driverCookie.getExpiry());
                    cookieEntity.setCredential(credential); // Associate the cookie with the credential

                    cookieService.updateCookie(cookieEntity);
                });
            }
            executor.shutdown();
        }catch (Exception exception){
            logger.error("Error while updating cookies to database");
        }

    }

    private boolean validateCookie(WebDriver driver, Credential credential) {
        driver.get(loginUrl);

        List<Cookie> cookiesFromDb = cookieService.getAllCookies(credential);
        for (Cookie cookieEntity : cookiesFromDb) {
            Date expiryDate = cookieEntity.getExpiry();
            org.openqa.selenium.Cookie seleniumCookie = new org.openqa.selenium.Cookie(
                    cookieEntity.getName(),
                    cookieEntity.getValue(),
                    cookieEntity.getDomain(),
                    cookieEntity.getPath(),
                    expiryDate);

            driver.manage().addCookie(seleniumCookie);
        }

        driver.navigate().refresh();
        if (driver.getCurrentUrl().contains("/login")) {
            // If the URL contains 'login', consider that the user is not logged in
            cookieService.deleteAllCookies(credential);
            return false;  // Login failed
        } else {
            // If the URL contains 'person', assume successful login
            return true;  // Login successful
        }
    }
}

package com.wishtreetech.rocketreach.login.service;

import com.wishtreetech.rocketreach.login.entity.Credential;
import org.openqa.selenium.WebDriver;

/**
 * The interface Login service.
 */
public interface LoginService {
    /**
     * Logs in the given WebDriver instance.
     *
     * @param driver the WebDriver instance.
     * @return true if login is successful.
     */
    boolean login(WebDriver driver, Credential credential);
}

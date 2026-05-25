package com.expandtesting.tests.ui;

import com.expandtesting.base.BaseTest;
import com.expandtesting.pages.LoginPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTest extends BaseTest {

    @Test(description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        var loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();

        // Put your real registered credentials back in here!
        loginPage.performLogin("bhushan-test@example.com", "Capgemini123!");

        // Wait for the app to process the login and route to the dashboard
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));

        // Assert that we are no longer on the login page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("login"), "Login failed: We were not redirected to the dashboard.");
    }
}
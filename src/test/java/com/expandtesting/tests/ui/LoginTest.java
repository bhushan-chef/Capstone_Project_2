package com.expandtesting.tests.ui;

import com.expandtesting.base.BaseTest;
import com.expandtesting.config.ConfigReader;
import com.expandtesting.pages.LoginPage;
import io.qameta.allure.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

@Epic("Notes App - UI Testing")
@Feature("Login Functionality")
public class LoginTest extends BaseTest {

    // ── TC-UI-01 ── POSITIVE ──────────────────────────────────────────────────
    @Test(description = "TC-UI-01: Valid login redirects to dashboard")
    @Story("FR-01: UI Login should work")
    @Severity(SeverityLevel.CRITICAL)
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin(ConfigReader.getEmail(), ConfigReader.getPassword());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("login"),
                "TC-UI-01 FAILED: User was not redirected to dashboard after valid login.");
    }

    // ── TC-UI-02 ── NEGATIVE ─────────────────────────────────────────────────
    @Test(description = "TC-UI-02: Invalid password shows error message")
    @Story("FR-09: Negative scenarios")
    @Severity(SeverityLevel.CRITICAL)
    public void testInvalidPasswordShowsError() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin(ConfigReader.getEmail(), "WrongPassword999!");

        String alert = loginPage.getAlertMessage();
        Assert.assertTrue(
                alert.toLowerCase().contains("incorrect") ||
                        alert.toLowerCase().contains("invalid") ||
                        alert.toLowerCase().contains("password"),
                "TC-UI-02 FAILED: Expected error message not shown. Got: " + alert);
    }

    // ── TC-UI-03 ── NEGATIVE ─────────────────────────────────────────────────
    @Test(description = "TC-UI-03: Non-registered email shows error message")
    @Story("FR-09: Negative scenarios")
    @Severity(SeverityLevel.NORMAL)
    public void testUnregisteredEmailShowsError() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin("notregistered_xyz@fake.com", "SomePass123!");

        String alert = loginPage.getAlertMessage();
        Assert.assertFalse(alert.isEmpty(),
                "TC-UI-03 FAILED: No error message shown for unregistered email.");
    }

    // ── TC-UI-04 ── NEGATIVE ─────────────────────────────────────────────────
    @Test(description = "TC-UI-04: Empty credentials shows validation error")
    @Story("FR-09: Negative scenarios")
    @Severity(SeverityLevel.NORMAL)
    public void testEmptyCredentialsShowsError() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin("", "");

        // Page should stay on login — not redirect to dashboard
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("login"),
                "TC-UI-04 FAILED: Empty credentials should not allow login.");
    }

    // ── TC-UI-05 ── NEGATIVE ─────────────────────────────────────────────────
    @Test(description = "TC-UI-05: Invalid email format shows validation error")
    @Story("FR-09: Negative scenarios")
    @Severity(SeverityLevel.MINOR)
    public void testInvalidEmailFormatShowsError() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin("notanemail", "SomePass123!");

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("login"),
                "TC-UI-05 FAILED: Invalid email format should not allow login.");
    }
}
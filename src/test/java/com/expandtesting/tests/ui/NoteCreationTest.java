package com.expandtesting.tests.ui;

import com.expandtesting.base.BaseTest;
import com.expandtesting.config.ConfigReader;
import com.expandtesting.pages.DashboardPage;
import com.expandtesting.pages.LoginPage;
import io.qameta.allure.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

@Epic("Notes App - UI Testing")
@Feature("Note Creation")
public class NoteCreationTest extends BaseTest {

    @BeforeMethod
    public void loginBeforeTest() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin(ConfigReader.getEmail(), ConfigReader.getPassword());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));
    }

    // ── TC-UI-06 ── POSITIVE ──────────────────────────────────────────────────
    @Test(description = "TC-UI-06: Create a Work note and verify it appears on dashboard")
    @Story("FR-02 + FR-03: Create note via UI and it appears instantly")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateWorkNoteAppearsOnDashboard() {
        String title = "Work Note TC06 " + System.currentTimeMillis();

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.createNote("Work", title, "Created in TC-UI-06");

        driver.navigate().refresh();

        Assert.assertTrue(dashboardPage.isNoteDisplayed(title),
                "TC-UI-06 FAILED: Work note not visible on dashboard after creation.");
    }

    // ── TC-UI-07 ── POSITIVE ──────────────────────────────────────────────────
    @Test(description = "TC-UI-07: Create a Personal note and verify it appears on dashboard")
    @Story("FR-02 + FR-03: Create note via UI and it appears instantly")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePersonalNoteAppearsOnDashboard() {
        String title = "Personal Note TC07 " + System.currentTimeMillis();

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.createNote("Personal", title, "Created in TC-UI-07");

        driver.navigate().refresh();

        Assert.assertTrue(dashboardPage.isNoteDisplayed(title),
                "TC-UI-07 FAILED: Personal note not visible on dashboard after creation.");
    }

    // ── TC-UI-08 ── POSITIVE ──────────────────────────────────────────────────
    @Test(description = "TC-UI-08: Create a Home note and verify it appears on dashboard")
    @Story("FR-02 + FR-03: Create note via UI and it appears instantly")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateHomeNoteAppearsOnDashboard() {
        String title = "Home Note TC08 " + System.currentTimeMillis();

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.createNote("Home", title, "Created in TC-UI-08");

        driver.navigate().refresh();

        Assert.assertTrue(dashboardPage.isNoteDisplayed(title),
                "TC-UI-08 FAILED: Home note not visible on dashboard after creation.");
    }
}
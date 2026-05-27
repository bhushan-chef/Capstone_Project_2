package com.expandtesting.tests.e2e;

import com.expandtesting.base.BaseTest;
import com.expandtesting.config.ConfigReader;
import com.expandtesting.pages.DashboardPage;
import com.expandtesting.pages.LoginPage;
import com.expandtesting.tests.api.NotesAPI;
import io.qameta.allure.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Epic("Notes App - Hybrid E2E Testing")
@Feature("UI and API Synchronization")
public class HybridSyncTest extends BaseTest {

    // ── TC-E2E-01 ── POSITIVE ─────────────────────────────────────────────────
    @Test(description = "TC-E2E-01: Note created in UI appears in API GET /notes response")
    @Story("FR-05: UI-created note must appear in API")
    @Severity(SeverityLevel.CRITICAL)
    public void testNoteCreatedInUiAppearsInApi() {
        String email    = ConfigReader.getEmail();
        String password = ConfigReader.getPassword();
        String title    = "E2E Note TC01 " + System.currentTimeMillis();

        // 1. UI: Login and create note
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin(email, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));

        new DashboardPage(driver).createNote("Work", title, "E2E sync test TC01");
        driver.navigate().refresh();

        // 2. API: Verify note exists in backend
        NotesAPI notesAPI = new NotesAPI(email, password);
        List<Map<String, Object>> allNotes = notesAPI.getAllNotes().jsonPath().getList("data");

        boolean foundInApi = allNotes.stream()
                .anyMatch(n -> title.equals(n.get("title")));

        Assert.assertTrue(foundInApi,
                "TC-E2E-01 FAILED: Note created in UI was not found in API response.");

        // Cleanup
        String noteId = allNotes.stream()
                .filter(n -> title.equals(n.get("title")))
                .map(n -> (String) n.get("id"))
                .findFirst().orElse(null);
        if (noteId != null) notesAPI.deleteNoteById(noteId);
    }

    // ── TC-E2E-02 ── POSITIVE ─────────────────────────────────────────────────
    @Test(description = "TC-E2E-02: Note deleted via API disappears from UI")
    @Story("FR-06 + FR-07: Delete note via API, deleted note disappears from UI")
    @Severity(SeverityLevel.CRITICAL)
    public void testNoteDeletedViaApiDisappearsFromUi() {
        String email    = ConfigReader.getEmail();
        String password = ConfigReader.getPassword();
        String title    = "E2E Note TC02 " + System.currentTimeMillis();

        // 1. UI: Login and create note
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin(email, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.createNote("Personal", title, "E2E sync test TC02");
        driver.navigate().refresh();

        Assert.assertTrue(dashboardPage.isNoteDisplayed(title),
                "TC-E2E-02 SETUP FAILED: Note not visible in UI before deletion.");

        // 2. API: Find and delete the note
        NotesAPI notesAPI = new NotesAPI(email, password);
        List<Map<String, Object>> allNotes = notesAPI.getAllNotes().jsonPath().getList("data");

        String noteId = allNotes.stream()
                .filter(n -> title.equals(n.get("title")))
                .map(n -> (String) n.get("id"))
                .findFirst().orElse(null);

        Assert.assertNotNull(noteId,
                "TC-E2E-02 FAILED: Could not find note in API to delete.");

        notesAPI.deleteNoteById(noteId);

        // 3. UI: Refresh and verify note is gone
        driver.navigate().refresh();
        Assert.assertFalse(dashboardPage.isNoteDisplayed(title),
                "TC-E2E-02 FAILED: Deleted note still appears in UI after API deletion.");
    }

    // ── TC-E2E-03 ── POSITIVE ─────────────────────────────────────────────────
    @Test(description = "TC-E2E-03: UI and API note data fields match exactly")
    @Story("FR-05: UI-created note data must be consistent in API")
    @Severity(SeverityLevel.CRITICAL)
    public void testUiAndApiNoteDataConsistency() {
        String email       = ConfigReader.getEmail();
        String password    = ConfigReader.getPassword();
        String title       = "Consistency TC03 " + System.currentTimeMillis();
        String description = "Data consistency check";
        String category    = "Home";

        // 1. UI: Login and create note
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin(email, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));

        new DashboardPage(driver).createNote(category, title, description);
        driver.navigate().refresh();

        // 2. API: Find the note and compare all fields
        NotesAPI notesAPI = new NotesAPI(email, password);
        List<Map<String, Object>> allNotes = notesAPI.getAllNotes().jsonPath().getList("data");

        Map<String, Object> apiNote = allNotes.stream()
                .filter(n -> title.equals(n.get("title")))
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(apiNote,
                "TC-E2E-03 FAILED: Note not found in API.");
        Assert.assertEquals(apiNote.get("title"), title,
                "TC-E2E-03 FAILED: Title mismatch between UI and API.");
        Assert.assertEquals(apiNote.get("description"), description,
                "TC-E2E-03 FAILED: Description mismatch between UI and API.");
        Assert.assertEquals(apiNote.get("category"), category,
                "TC-E2E-03 FAILED: Category mismatch between UI and API.");

        // Cleanup
        notesAPI.deleteNoteById((String) apiNote.get("id"));
    }
}
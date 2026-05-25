package com.expandtesting.tests.e2e;

import com.expandtesting.api.NotesAPI;
import com.expandtesting.base.BaseTest;
import com.expandtesting.pages.DashboardPage;
import com.expandtesting.pages.LoginPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class HybridSyncTest extends BaseTest {

    @Test(description = "E2E: Create note in UI -> Verify in API -> Delete via API -> Verify UI update")
    public void testUiApiDataConsistency() {
        // Test Data (Make sure your real credentials are here!)
        String email = "bhushan-test@example.com";
        String password = "Capgemini123!";
        String category = "Work";
        String title = "Capstone Integration Note " + System.currentTimeMillis();
        String description = "Testing hybrid sync capabilities.";

        // 1. UI STEP: Login and Create Note
        var loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.performLogin(email, password);

        // WAIT FOR THE DASHBOARD TO LOAD
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));

        var dashboardPage = new DashboardPage(driver);
        dashboardPage.createNote(category, title, description);

        // --- THE FIX FOR THE SYNC BUG ---
        // Force the browser to refresh and fetch the latest backend data immediately to bypass UI lag
        driver.navigate().refresh();

        Assert.assertTrue(dashboardPage.isNoteDisplayed(title), "UI check failed: Note not visible on dashboard.");

        // 2. API STEP: Fetch notes and verify the UI-created note exists in the database
        var notesApi = new NotesAPI(email, password);
        var apiResponse = notesApi.getAllNotes();

        // Extract the notes list and find our specific note ID
        List<Map<String, Object>> allNotes = apiResponse.jsonPath().getList("data");
        String createdNoteId = null;

        for (Map<String, Object> note : allNotes) {
            if (title.equals(note.get("title"))) {
                createdNoteId = (String) note.get("id");
                break;
            }
        }

        Assert.assertNotNull(createdNoteId, "API check failed: Note created in UI was not found in API response.");

        // 3. API STEP: Delete the note via backend
        notesApi.deleteNoteById(createdNoteId);

        // 4. UI STEP: Refresh the page and verify the note is gone
        driver.navigate().refresh();
        Assert.assertFalse(dashboardPage.isNoteDisplayed(title), "E2E check failed: Deleted note still appears in UI.");
    }
}
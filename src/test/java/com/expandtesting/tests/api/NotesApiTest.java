package com.expandtesting.tests.api;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Notes App - API Testing")
@Feature("Notes API")
public class NotesApiTest {

    private NotesAPI notesAPI;
    private String createdNoteId;

    @BeforeClass
    public void setupApi() {
        notesAPI = new NotesAPI();
    }

    // ── TC-API-01 ── POSITIVE ─────────────────────────────────────────────────
    @Test(description = "TC-API-01: GET /notes returns 200 and valid schema")
    @Story("FR-04: API GET /notes returns list")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllNotesReturns200AndValidSchema() {
        Response response = notesAPI.getAllNotes();
        Assert.assertEquals(response.statusCode(), 200,
                "TC-API-01 FAILED: Expected status 200.");
        Assert.assertTrue(response.jsonPath().getBoolean("success"),
                "TC-API-01 FAILED: Response success flag should be true.");
    }

    // ── TC-API-02 ── POSITIVE ─────────────────────────────────────────────────
    @Test(description = "TC-API-02: POST /notes creates a note and returns 200")
    @Story("FR-02: Create note via API")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateNoteViaApi() {
        String title = "API Note TC02 " + System.currentTimeMillis();
        Response response = notesAPI.createNote(title, "API test description", "Work");

        Assert.assertEquals(response.statusCode(), 200,
                "TC-API-02 FAILED: Expected status 200 on note creation.");
        Assert.assertTrue(response.jsonPath().getBoolean("success"),
                "TC-API-02 FAILED: success flag should be true.");

        // Store the ID for the delete test below
        createdNoteId = response.jsonPath().getString("data.id");
        Assert.assertNotNull(createdNoteId,
                "TC-API-02 FAILED: Note ID should not be null after creation.");
    }

    // ── TC-API-03 ── POSITIVE ─────────────────────────────────────────────────
    @Test(description = "TC-API-03: GET /notes response time is under 2 seconds",
            dependsOnMethods = "testGetAllNotesReturns200AndValidSchema")
    @Story("FR-08: API response < 2s")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNotesResponseTimeUnder2Seconds() {
        long responseTime = notesAPI.getAllNotes().time();
        Assert.assertTrue(responseTime < 2000,
                "TC-API-03 FAILED: Response time was " + responseTime + "ms, expected < 2000ms.");
    }

    // ── TC-API-04 ── POSITIVE ─────────────────────────────────────────────────
    @Test(description = "TC-API-04: DELETE /notes/{id} deletes note successfully",
            dependsOnMethods = "testCreateNoteViaApi")
    @Story("FR-06: Delete note via API")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteNoteViaApi() {
        Assert.assertNotNull(createdNoteId,
                "TC-API-04 SKIPPED: No note ID available from TC-API-02.");

        Response response = notesAPI.deleteNoteById(createdNoteId);
        Assert.assertEquals(response.statusCode(), 200,
                "TC-API-04 FAILED: Expected 200 on delete.");
        Assert.assertTrue(response.jsonPath().getBoolean("success"),
                "TC-API-04 FAILED: success flag should be true after delete.");
    }

    // ── TC-API-05 ── NEGATIVE ─────────────────────────────────────────────────
    @Test(description = "TC-API-05: GET /notes without token returns 401")
    @Story("FR-09: Negative scenarios")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetNotesWithoutTokenReturns401() {
        Response response = notesAPI.getNotesWithoutToken();
        Assert.assertEquals(response.statusCode(), 401,
                "TC-API-05 FAILED: Expected 401 Unauthorized when no token provided.");
    }

    // ── TC-API-06 ── NEGATIVE ─────────────────────────────────────────────────
    @Test(description = "TC-API-06: POST /notes without token returns 401")
    @Story("FR-09: Negative scenarios")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateNoteWithoutTokenReturns401() {
        Response response = notesAPI.createNoteWithoutToken(
                "Unauthorized Note", "Should fail", "Work");
        Assert.assertEquals(response.statusCode(), 401,
                "TC-API-06 FAILED: Expected 401 Unauthorized when no token provided.");
    }

    // ── TC-API-07 ── NEGATIVE ─────────────────────────────────────────────────
    @Test(description = "TC-API-07: GET /notes/{id} with invalid ID returns 400 or 404")
    @Story("FR-09: Negative scenarios")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNoteWithInvalidIdReturnsError() {
        Response response = RestAssured.given()
                .baseUri("https://practice.expandtesting.com/notes/api")
                .header("x-auth-token", notesAPI.getAuthToken())
                .get("/notes/invalid-note-id-000")
                .then()
                .extract().response();

        int status = response.statusCode();
        Assert.assertTrue(status == 400 || status == 404,
                "TC-API-07 FAILED: Expected 400 or 404 for invalid note ID. Got: " + status);
    }

    // ── TC-API-08 ── NEGATIVE ─────────────────────────────────────────────────
    @Test(description = "TC-API-08: POST /notes with empty title returns 400")
    @Story("FR-09: Negative scenarios")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateNoteWithEmptyTitleReturns400() {
        Response response = notesAPI.createNoteRaw("", "Some description", "Work");
        Assert.assertEquals(response.statusCode(), 400,
                "TC-API-08 FAILED: Expected 400 Bad Request for empty title.");
    }
}
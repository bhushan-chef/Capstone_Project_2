package com.expandtesting.tests.api;

import com.expandtesting.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

public class NotesAPI {

    private String authToken;

    private RequestSpecification baseSpec() {
        return RestAssured.given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(ContentType.JSON)
                .header("x-auth-token", authToken);
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    public NotesAPI(String email, String password) {
        loginAndGetToken(email, password);
    }

    // Default constructor uses config.properties credentials
    public NotesAPI() {
        loginAndGetToken(ConfigReader.getEmail(), ConfigReader.getPassword());
    }

    private void loginAndGetToken(String email, String password) {
        String payload = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

        Response response = RestAssured.given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/users/login");

        response.then().statusCode(200);
        this.authToken = response.jsonPath().getString("data.token");
    }

    // ── Notes CRUD ────────────────────────────────────────────────────────────

    public Response getAllNotes() {
        return baseSpec()
                .get("/notes")
                .then()
                .statusCode(200)
                .time(Matchers.lessThan(2000L))
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/notes-schema.json"))
                .extract().response();
    }

    public Response createNote(String title, String description, String category) {
        String payload = String.format(
                "{\"title\":\"%s\",\"description\":\"%s\",\"category\":\"%s\"}",
                title, description, category);

        return baseSpec()
                .body(payload)
                .post("/notes")
                .then()
                .statusCode(200)
                .time(Matchers.lessThan(2000L))
                .extract().response();
    }

    public Response getNoteById(String noteId) {
        return baseSpec()
                .get("/notes/" + noteId)
                .then()
                .statusCode(200)
                .extract().response();
    }

    public Response deleteNoteById(String noteId) {
        return baseSpec()
                .delete("/notes/" + noteId)
                .then()
                .statusCode(200)
                .extract().response();
    }

    public Response getAllNotesRaw() {
        // Raw call — no assertions, used for negative tests
        return baseSpec()
                .get("/notes")
                .then()
                .extract().response();
    }

    public Response createNoteRaw(String title, String description, String category) {
        String payload = String.format(
                "{\"title\":\"%s\",\"description\":\"%s\",\"category\":\"%s\"}",
                title, description, category);

        return baseSpec()
                .body(payload)
                .post("/notes")
                .then()
                .extract().response();
    }

    public Response getNotesWithoutToken() {
        // Intentionally no auth token — for negative testing
        return RestAssured.given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(ContentType.JSON)
                .get("/notes")
                .then()
                .extract().response();
    }

    public Response createNoteWithoutToken(String title, String description, String category) {
        String payload = String.format(
                "{\"title\":\"%s\",\"description\":\"%s\",\"category\":\"%s\"}",
                title, description, category);

        return RestAssured.given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/notes")
                .then()
                .extract().response();
    }

    public String getAuthToken() {
        return authToken;
    }
}
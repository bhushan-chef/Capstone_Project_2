package com.expandtesting.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

public class NotesAPI {

    private static final String BASE_URL = "https://practice.expandtesting.com/notes/api";
    private String authToken;

    public NotesAPI(String email, String password) {
        RestAssured.baseURI = BASE_URL;
        loginAndGetToken(email, password);
    }

    // Authenticate via API to get the token for subsequent requests
    private void loginAndGetToken(String email, String password) {
        String payload = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/users/login");

        response.then().statusCode(200);
        this.authToken = response.jsonPath().getString("data.token");
    }

    // GET Request: Fetch all notes and assert response time is under 2 seconds
    public Response getAllNotes() {
        return RestAssured.given()
                .header("x-auth-token", authToken)
                .get("/notes")
                .then()
                .statusCode(200)
                .time(Matchers.lessThan(2000L)) // Mandated < 2s response check
                .extract().response();
    }

    // DELETE Request: Delete a specific note by ID
    public void deleteNoteById(String noteId) {
        RestAssured.given()
                .header("x-auth-token", authToken)
                .delete("/notes/" + noteId)
                .then()
                .statusCode(200);
    }
}
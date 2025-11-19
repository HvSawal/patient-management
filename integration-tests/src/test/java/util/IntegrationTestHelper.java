package util;


import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class IntegrationTestHelper {

    /**
     * Sends a POST request with a JSON body and asserts the expected HTTP status.
     *
     * @param path               endpoint path (e.g. "/auth/login")
     * @param jsonBody           raw JSON string body
     * @param expectedStatusCode expected HTTP status code to assert
     * @return extracted Response for further assertions
     */
    public static Response postJson(String path, String jsonBody, int expectedStatusCode) {
        return given()
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post(path)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public static Response postJsonWithAuth(String path, String jsonBody, String token, int expectedStatusCode) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post(path)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public static Response getJson(String path, int expectedStatusCode) {
        return given()
                .accept("application/json")
                .when()
                .get(path)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public static Response getJsonWithAuth(String path, String token, int expectedStatusCode) {
        return given()
                .header("Authorization", ("Bearer " + token))
                .accept("application/json")
                .when()
                .get(path)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    // -------- PUT helpers --------

    public static Response putJson(String path,
                                   String jsonBody,
                                   int expectedStatusCode) {
        return given()
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .put(path)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public static Response putJsonWithAuth(String path,
                                           String jsonBody,
                                           String token,
                                           int expectedStatusCode) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .put(path)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    // -------- DELETE helpers --------

    public static Response deleteWithAuthToken(String path,
                                               String token,
                                               int expectedStatusCode) {
        return given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(path)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public static Response deleteJsonWithAuth(String path,
                                              String jsonBody,
                                              String token,
                                              int expectedStatusCode) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .delete(path)
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }


    /**
     * Variant without status assertion (if you want to assert in the test).
     */
    public static Response postJson(String path, String jsonBody) {
        return given()
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }
}

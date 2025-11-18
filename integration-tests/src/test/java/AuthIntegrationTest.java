import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {

    private final static String BASE_URI = "http://localhost:4004";

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    // TODO - testing logic for restassured
    // 1. Arrange
    // 2. Act
    // 3. Assert

    @Test
    public void shouldReturnOkWithValidToken() {

        String loginPayload = """
                    {
                        "email" : "testuser@test.com",
                        "password" : "password123"
                    }
                """;

        Response response = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().response();

        System.out.println("Generated Token : " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorizedOnInvalidToken() {

        String loginPayload = """
                    {
                        "email" : "invalid_user@test.com",
                        "password" : "wrongpassword"
                    }
                """;

        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

}

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static util.IntegrationTestHelper.postJson;

public class AuthIntegrationTest {

    private final static String BASE_URI = "http://localhost:4004";

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

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
        Response response = postJson("/auth/login", loginPayload, 200);
        assertNotNull(response.jsonPath().getString("token"), "Token not generated!");
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

        postJson("/auth/login", loginPayload, 401);
    }

}

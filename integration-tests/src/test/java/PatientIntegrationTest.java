import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static util.IntegrationTestHelper.deleteWithAuthToken;
import static util.IntegrationTestHelper.getJson;
import static util.IntegrationTestHelper.getJsonWithAuth;
import static util.IntegrationTestHelper.postJson;
import static util.IntegrationTestHelper.postJsonWithAuth;
import static util.IntegrationTestHelper.putJsonWithAuth;

public class PatientIntegrationTest {

    //TODO -
    // convert Json string to json objects for better control and code readability

    private static final String BASE_URI = "http://localhost:4004";
    private static String token;
    private static final String LOGIN_PAYLOAD = """
                    {
                        "email" : "testuser@test.com",
                        "password" : "password123"
                    }
                """;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = BASE_URI;
        token = postJson("/auth/login", LOGIN_PAYLOAD, 200)
                .jsonPath()
                .getString("token");
    }


    @Test
    @Order(1)
    public void shouldReturnPatientsWithValidToken() {

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue());
    }

    @Test
    @Order(2)
    public void shouldCreateGetDeletePatientWithValidToken() {

        String createPatientPayload = """
                    {
                        "firstName" : "John",
                        "lastName" : "Cena",
                        "email" : "john.cena.iTest@test.com",
                        "address" : "123 Main Street",
                        "dateOfBirth" : "1995-09-09",
                        "registeredDate" : "2025-10-10"
                    }
                """;
        Response response = postJsonWithAuth("/api/patients", createPatientPayload, token,200);
        JsonPath jsonPath = response.jsonPath();
        assertNotNull(jsonPath.getString("id"), "Patient id should be returned!");
        assertEquals("John", jsonPath.getString("firstName"));
        assertEquals("Cena", jsonPath.getString("lastName"));
        assertEquals("john.cena.iTest@test.com", jsonPath.getString("email"));
        assertEquals("123 Main Street", jsonPath.getString("address"));
        assertEquals("1995-09-09", jsonPath.getString("dateOfBirth"));

        Response getResponse = getJsonWithAuth(("/api/patients/" + jsonPath.getString("id")), token, 200);
        JsonPath getJsonPath = response.jsonPath();
        assertEquals("John", getJsonPath.getString("firstName"));
        assertEquals("Cena", getJsonPath.getString("lastName"));
        assertEquals("1995-09-09", getJsonPath.getString("dateOfBirth"));
        
        Response deleteResponse = deleteWithAuthToken(("/api/patients/" + jsonPath.getString("id")), token, 204);
        assertEquals(204, deleteResponse.statusCode());
    }

    @Test
    @Order(3)
    public void shouldUpdatePatientWithValidToken(){

        String id = "123e4567-e89b-12d3-a456-426614174000";

        String updatePatientPayload = """
                    {
                        "firstName" : "John",
                        "lastName" : "Doe Woe",
                        "email" : "john.doe_updated@email.com",
                        "address" : "123 Main Street",
                        "dateOfBirth" : "1995-09-09"
                    }
                """;
        Response response = putJsonWithAuth("/api/patients/" + id, updatePatientPayload, token,200);

        JsonPath jsonPath = response.jsonPath();
        assertNotNull(jsonPath.getString("id"), "Patient id should be returned!");
        assertEquals("John", jsonPath.getString("firstName"));
        assertEquals("Doe Woe", jsonPath.getString("lastName"));
        assertEquals("john.doe_updated@email.com", jsonPath.getString("email"));
        assertEquals("123 Main Street", jsonPath.getString("address"));
        assertEquals("1995-09-09", jsonPath.getString("dateOfBirth"));
    }
}

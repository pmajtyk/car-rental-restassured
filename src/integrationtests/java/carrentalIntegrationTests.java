import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class carrentalIntegrationTests {

    private RequestSpecification requestSpecification;
    private String carEndpoint;
    private String endpoint;
    private static final String CAR_ENDPOINT = "/api/v1/car";

    @BeforeEach
    public void setup() {
        RestAssured.reset();
        endpoint = "http://localhost:8080";

        requestSpecification = given().header("X-Request-ID", "smoke-" + System.currentTimeMillis())
                .header(HTTP.CONTENT_TYPE, "application/json");

        carEndpoint = endpoint + CAR_ENDPOINT;
    }

    @Test
    public void smokeTest() {
        System.out.println("appUrl = " + endpoint);
        Response responseBody = given()
                .when()
                .get(carEndpoint)
                .then()
                .extract().response();

        System.out.println("status = " +responseBody.getStatusCode());
//        System.out.println("body = " + responseBody.body().asString());

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
    }


}

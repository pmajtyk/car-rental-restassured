package com.capgemini.carrental;

import com.capgemini.demo.carrental.model.Car;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RentalCarsApplication.class)
@ActiveProfiles("test")
public class carrentalIntegrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RequestSpecification requestSpecification;
    private String carEndpoint;
    private String tenantEndpoint;
    private String rentalEndpoint;
    private String endpoint;
    private Long addedCarId;
    private static final String CAR_ENDPOINT = "/api/v1/car";
    private static final String TENANT_ENDPOINT = "/api/v1/tenant";
    private static final String RENTAL_ENDPOINT = "/api/v1/rental/search";

    @BeforeEach
    public void setup() {
        RestAssured.reset();
        endpoint = "http://localhost:8080";

        requestSpecification = given().header("X-Request-ID", "smoke-" + System.currentTimeMillis())
                .header(HTTP.CONTENT_TYPE, "application/json");

        carEndpoint = endpoint + CAR_ENDPOINT;
        tenantEndpoint = endpoint + TENANT_ENDPOINT;
        rentalEndpoint = endpoint + RENTAL_ENDPOINT;
    }

    @AfterEach
    public void tearDown() {
        if (addedCarId != null) {
            jdbcTemplate.update("DELETE FROM cars WHERE id = ?", addedCarId);
        }
    }


    @Test
    public void smokeTest_car() {
        System.out.println("appUrl = " + endpoint);
        Response responseBody = given()
                .when()
                .get(carEndpoint)
                .then()
                .extract().response();

        System.out.println("status = " +responseBody.getStatusCode());

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void smokeTest_tenant() {
        System.out.println("appUrl = " + endpoint);
        Response responseBody = given()
                .when()
                .get(tenantEndpoint)
                .then()
                .extract().response();

        System.out.println("status = " +responseBody.getStatusCode());

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void smokeTest_rental() {
        System.out.println("appUrl = " + endpoint);
        Response responseBody = given()
                .when()
                .get(rentalEndpoint)
                .then()
                .extract().response();

        System.out.println("status = " +responseBody.getStatusCode());

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void getACarTest() {
        System.out.println("appUrl = " + endpoint);
        int carId = 100;
        Response responseBody = given()
                .when()
                .get(carEndpoint + "/" + Integer.toString(carId))
                .then()
                .extract().response();
        JsonPath car = responseBody.getBody().jsonPath();
        System.out.println("status = " +responseBody.getStatusCode());
        System.out.println("model = " + car.getString("model"));

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
        assertThat(car.getString("model")).isEqualTo("Astra");

    }

    @Test
    public void getATenantTest() {
        System.out.println("appUrl = " + endpoint);
        int tenantId = 100;
        Response responseBody = given()
                .when()
                .get(tenantEndpoint + "/" + Integer.toString(tenantId))
                .then()
                .extract().response();
        JsonPath tenant = responseBody.getBody().jsonPath();
        System.out.println("status = " +responseBody.getStatusCode());
        System.out.println("name = " + tenant.getString("name"));

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
        assertThat(tenant.getString("name")).isEqualTo("Adam");

    }

    @Test
    public void postACarTest() {
        System.out.println("appUrl = " + endpoint);
        Car newCar = new Car();
        newCar.setBrand("Subaru");
        newCar.setModel("Outback");
        newCar.setBodyType("COMBI");
        newCar.setFuelType("PETROL");
        newCar.setYear(2020);
        Response responseBody = given()
                .contentType("application/json")
                .body(newCar)
                .when()
                .post(carEndpoint)
                .then()
                .extract().response();
        JsonPath car = responseBody.getBody().jsonPath();
        addedCarId = Long.parseLong(car.getString("id"));
        System.out.println("status = " +responseBody.getStatusCode());
        System.out.println("id of created car = " +car.getString("id"));
        System.out.println("model = " + car.getString("model"));

    }

//    @Test
//    public void getARentalTest() {
//        System.out.println("appUrl = " + endpoint);
//        int rentalId = 0;
//        Response responseBody = given()
//                .when()
//                .get(rentalEndpoint + "/" + Integer.toString(rentalId))
//                .then()
//                .extract().response();
//        JsonPath rental = responseBody.getBody().jsonPath();
//        System.out.println("status = " +responseBody.getStatusCode());
//        System.out.println("model = " + rental.getString("model"));
//
//        assertThat(responseBody.getStatusCode()).isEqualTo(200);
//        assertThat(rental.getString("model")).isEqualTo("Astra");
//
//    }

}

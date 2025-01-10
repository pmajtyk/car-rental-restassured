package com.capgemini.demo.carrental;

import com.capgemini.carrental.RentalCarsApplication;
import com.capgemini.carrental.model.Gender;
import com.capgemini.carrental.model.Tenant;
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
public class CarRentalIntegrationTests {

    @SuppressWarnings("unused")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RequestSpecification requestSpecification;
    private String carEndpoint;
    private String tenantEndpoint;
    private String rentalEndpoint;
    private String endpoint;
    private Long addedCarId;
    private Long addedTenantId;
    private Long addedRentalId;
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
        if (addedTenantId != null) {
            jdbcTemplate.update("DELETE FROM tenants WHERE id = ?", addedTenantId);
        }
        if (addedRentalId != null) {
            jdbcTemplate.update("DELETE FROM rentals WHERE id = ?", addedRentalId);
        }
    }


    @Test
    public void smokeTest_car() {
        System.out.println("appUrl = " + endpoint);
        Response responseBody = requestSpecification
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
        Response responseBody = requestSpecification
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
        Response responseBody = requestSpecification
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
        Response responseBody = requestSpecification
                .when()
                .get(carEndpoint + "/" + carId)
                .then()
                .extract().response();
        System.out.println("status = " +responseBody.getStatusCode());
        JsonPath car = responseBody.getBody().jsonPath();
        System.out.println("model = " + car.getString("model"));

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
        assertThat(car.getString("model")).isEqualTo("Astra");

    }

    @Test
    public void getATenantTest() {
        System.out.println("appUrl = " + endpoint);
        int tenantId = 100;
        Response responseBody = requestSpecification
                .when()
                .get(tenantEndpoint + "/" + tenantId)
                .then()
                .extract().response();
        System.out.println("status = " +responseBody.getStatusCode());
        JsonPath tenant = responseBody.getBody().jsonPath();
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
        Response addRresponseBody = requestSpecification
                .contentType("application/json")
                .body(newCar)
                .when()
                .post(carEndpoint)
                .then()
                .extract().response();
        JsonPath car = addRresponseBody.getBody().jsonPath();
        System.out.println("status = " +addRresponseBody.getStatusCode());
        addedCarId = Long.parseLong(car.getString("id"));
        System.out.println("id of created car = " +car.getString("id"));
        System.out.println("model = " + car.getString("model"));

        assertThat(addRresponseBody.getStatusCode()).isEqualTo(201);
        assertThat(car.getString("model")).isEqualTo("Outback");

    }

    @Test
    public void putACarTest() {
        System.out.println("appUrl = " + endpoint);
        Car newCar = new Car();
        newCar.setBrand("Subaru");
        newCar.setModel("Justy");
        newCar.setBodyType("SEDAN");
        newCar.setFuelType("PETROL");
        newCar.setYear(2000);
        Response addRresponseBody = requestSpecification
                .contentType("application/json")
                .body(newCar)
                .when()
                .post(carEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addRresponseBody.getStatusCode());
        JsonPath car = addRresponseBody.getBody().jsonPath();
        addedCarId = Long.parseLong(car.getString("id"));
        System.out.println("id of created car = " +car.getString("id"));
        newCar.setBodyType("HATCHBACK");
        newCar.setYear(1999);
        Response updateResponseBody = requestSpecification
                .contentType("application/json")
                .body(newCar)
                .when()
                .put(carEndpoint+ "/" + addedCarId)
                .then()
                .extract().response();
        System.out.println("status = " +updateResponseBody.getStatusCode());
        JsonPath updatedCar = updateResponseBody.getBody().jsonPath();
        System.out.println("model = " + updatedCar.getString("model"));

        assertThat(updateResponseBody.getStatusCode()).isEqualTo(200);
        assertThat(updatedCar.getString("bodyType")).isEqualTo("HATCHBACK");
        assertThat(updatedCar.getString("year")).isEqualTo("1999");

    }

    @Test
    public void deleteACarTest() {
        System.out.println("appUrl = " + endpoint);
        Car newCar = new Car();
        newCar.setBrand("Suzuki");
        newCar.setModel("Jimny");
        newCar.setBodyType("COMBI");
        newCar.setFuelType("PETROL");
        newCar.setYear(2005);
        Response addRresponseBody = requestSpecification
                .contentType("application/json")
                .body(newCar)
                .when()
                .post(carEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addRresponseBody.getStatusCode());
        JsonPath car = addRresponseBody.getBody().jsonPath();
        addedCarId = Long.parseLong(car.getString("id"));
        System.out.println("id of created car = " +car.getString("id"));
        Response deletedResponseBody = requestSpecification
                .contentType("application/json")
                .when()
                .delete(carEndpoint+ "/" + addedCarId)
                .then()
                .extract().response();
        System.out.println("status = " +deletedResponseBody.getStatusCode());

        assertThat(deletedResponseBody.getStatusCode()).isEqualTo(200);
        JsonPath deletedCar = deletedResponseBody.getBody().jsonPath();
        System.out.println("model = " + deletedCar.getString("model"));
        assertThat(deletedCar.getString("bodyType")).isEqualTo("COMBI");
        assertThat(deletedCar.getString("year")).isEqualTo("2005");

        Response responseBody = requestSpecification
                .when()
                .get(tenantEndpoint + "/" + addedCarId)
                .then()
                .extract().response();
        System.out.println("status = " +responseBody.getStatusCode());
        assertThat(responseBody.getStatusCode()).isEqualTo(404);


    }

    @Test
    public void postATenantTest() {
        System.out.println("appUrl = " + endpoint);
        Tenant newTenant = new Tenant();
        newTenant.setAge(21);
        newTenant.setGender(Gender.MALE);
        newTenant.setName("Kazimierz");
        Response addResponseBody = requestSpecification
                .contentType("application/json")
                .body(newTenant)
                .when()
                .post(tenantEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addResponseBody.getStatusCode());
        JsonPath tenant = addResponseBody.getBody().jsonPath();
        addedTenantId = Long.parseLong(tenant.getString("id"));
        System.out.println("id of created tenant = " +tenant.getString("id"));
        System.out.println("name = " + tenant.getString("name"));

        assertThat(addResponseBody.getStatusCode()).isEqualTo(201);
        assertThat(tenant.getString("name")).isEqualTo("Kazimierz");
    }

    @Test
    public void putATenantTest() {
        System.out.println("appUrl = " + endpoint);
        Tenant newTenant = new Tenant();
        newTenant.setAge(21);
        newTenant.setGender(Gender.MALE);
        newTenant.setName("Kazimierz");
        Response addResponseBody = requestSpecification
                .contentType("application/json")
                .body(newTenant)
                .when()
                .post(tenantEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addResponseBody.getStatusCode());
        JsonPath tenant = addResponseBody.getBody().jsonPath();
        addedTenantId = Long.parseLong(tenant.getString("id"));
        System.out.println("id of created tenant = " +tenant.getString("id"));
        System.out.println("name = " + tenant.getString("name"));
        newTenant.setAge(23);
        newTenant.setName("Kaźmirz");
        Response updateResponseBody = requestSpecification
                .contentType("application/json")
                .body(newTenant)
                .when()
                .put(tenantEndpoint+ "/" + addedTenantId)
                .then()
                .extract().response();
        System.out.println("status = " +updateResponseBody.getStatusCode());
        JsonPath updatedTenant = updateResponseBody.getBody().jsonPath();
        addedTenantId = Long.parseLong(updatedTenant.getString("id"));
        System.out.println("name = " + updatedTenant.getString("name"));


        assertThat(updateResponseBody.getStatusCode()).isEqualTo(200);
        assertThat(updatedTenant.getString("age")).isEqualTo("23");
        assertThat(updatedTenant.getString("name")).isEqualTo("Kaźmirz");
    }

    @Test
    public void deleteATenantTest() {
        System.out.println("appUrl = " + endpoint);
        Tenant newTenant = new Tenant();
        newTenant.setAge(21);
        newTenant.setGender(Gender.MALE);
        newTenant.setName("Kazimierz");
        Response addRresponseBody = requestSpecification
                .contentType("application/json")
                .body(newTenant)
                .when()
                .post(tenantEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addRresponseBody.getStatusCode());
        JsonPath tenant = addRresponseBody.getBody().jsonPath();
        addedTenantId = Long.parseLong(tenant.getString("id"));
        System.out.println("id of created tenant = " +tenant.getString("id"));
        Response deletedResponseBody = requestSpecification
                .contentType("application/json")
                .when()
                .delete(tenantEndpoint+ "/" + addedTenantId)
                .then()
                .extract().response();
        System.out.println("status = " +deletedResponseBody.getStatusCode());

        assertThat(deletedResponseBody.getStatusCode()).isEqualTo(200);
        JsonPath deletedTenant = deletedResponseBody.getBody().jsonPath();
        System.out.println("gender = " + deletedTenant.getString("gender"));
        assertThat(deletedTenant.getString("gender")).isEqualTo("MALE");
        assertThat(deletedTenant.getString("age")).isEqualTo("21");

        Response responseBody = requestSpecification
                .when()
                .get(tenantEndpoint + "/" + addedTenantId)
                .then()
                .extract().response();
        System.out.println("status = " +responseBody.getStatusCode());
        assertThat(responseBody.getStatusCode()).isEqualTo(404);
    }


//    @Test
//    public void getARentalTest() {
//        System.out.println("appUrl = " + endpoint);
//        int rentalId = 0;
//        Response responseBody = given()
//                .when()
//                .get(rentalEndpoint + "/" + rentalId)
//                .then()
//                .extract().response();
//        System.out.println("status = " +responseBody.getStatusCode());
//        JsonPath rental = responseBody.getBody().jsonPath();
//        System.out.println("model = " + rental.getString("model"));
//
//        assertThat(responseBody.getStatusCode()).isEqualTo(200);
//        assertThat(rental.getString("model")).isEqualTo("Astra");
//
//    }

}

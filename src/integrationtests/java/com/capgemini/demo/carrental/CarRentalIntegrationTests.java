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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private static final String RENTAL_ENDPOINT = "/api/v1/rental";

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
            jdbcTemplate.update("UPDATE cars set FK_RENTAL = null WHERE FK_RENTAL = ?", addedRentalId);
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
                .get(rentalEndpoint + "/search")
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


    @Test
    public void getARentalTest() {
        System.out.println("appUrl = " + endpoint);
        int rentalId = 1;
        Response responseBody = given()
                .when()
                .get(rentalEndpoint + "/search/" + rentalId)
                .then()
                .extract().response();
        System.out.println("status = " +responseBody.getStatusCode());
        JsonPath rental = responseBody.getBody().jsonPath();
        System.out.println("model = " + rental.getString("rentedCars[0].model"));

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
        assertThat(rental.getString("rentedCars[0].model")).isEqualTo("Corolla");
    }

    @Test
    public void getARentalByCarTest() {
        System.out.println("appUrl = " + endpoint);
        int carId=120;
        Response responseBody = given()
                .when()
                .get(rentalEndpoint + "/search/byCar/" + carId)
                .then()
                .extract().response();
        System.out.println("status = " +responseBody.getStatusCode());
        JsonPath rental = responseBody.getBody().jsonPath();
        System.out.println("model = " + rental.getString("rentedCars[0].model"));

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
        assertThat(rental.getString("rentedCars[0].model")).isEqualTo("Corolla");
    }

    @Test
    public void getARentalByTenantTest() {
        System.out.println("appUrl = " + endpoint);
        int tenantId=109;
        Response responseBody = given()
                .when()
                .get(rentalEndpoint + "/search/byTenant/" + tenantId)
                .then()
                .extract().response();
        System.out.println("status = " +responseBody.getStatusCode());
        JsonPath rental = responseBody.getBody().jsonPath();
        System.out.println("model = " + rental.getString("rentedCars[0].model"));

        assertThat(responseBody.getStatusCode()).isEqualTo(200);
        assertThat(rental.getString("rentedCars[0].model")).isEqualTo("Corolla");
    }

    @Test
    public void postARentalTest() {
        System.out.println("appUrl = " + endpoint);
        Map<String, Object> newRental = new HashMap<>();
        newRental.put("beginningOfRental", LocalDate.now().toString());
        newRental.put("carIds", Arrays.asList(103));
        newRental.put("endOfRental", LocalDate.now().plusDays(10).toString());
        newRental.put("tenantId", 102);

        Response addResponseBody = requestSpecification
                .contentType("application/json")
                .body(newRental)
                .when()
                .post(rentalEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addResponseBody.getStatusCode());
        JsonPath rental = addResponseBody.getBody().jsonPath();
        addedRentalId = Long.parseLong(rental.getString("id"));
        System.out.println("id of created tenant = " +rental.getString("id"));

        assertThat(addResponseBody.getStatusCode()).isEqualTo(201);
        assertThat(rental.getString("rentedCars[0].model")).isEqualTo("Clio");
    }

    @Test
    public void putARentalTest() {
        System.out.println("appUrl = " + endpoint);
        Map<String, Object> newRental = new HashMap<>();
        newRental.put("beginningOfRental", LocalDate.now().toString());
        newRental.put("carIds", Arrays.asList(103));
        newRental.put("endOfRental", LocalDate.now().plusDays(10).toString());
        newRental.put("tenantId", 102);

        Response addResponseBody = requestSpecification
                .contentType("application/json")
                .body(newRental)
                .when()
                .post(rentalEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addResponseBody.getStatusCode());
        JsonPath rental = addResponseBody.getBody().jsonPath();
        addedRentalId = Long.parseLong(rental.getString("id"));
        System.out.println("id of created tenant = " +rental.getString("id"));
        System.out.println("Cars rented in created tenant = " +rental.getString("rentedCars"));

        newRental.put("endOfRental", LocalDate.now().plusDays(20).toString());
        newRental.put("carIds", Arrays.asList(103, 104));
        Response updateResponseBody = requestSpecification
                .contentType("application/json")
                .body(newRental)
                .when()
                .put(rentalEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +updateResponseBody.getStatusCode());
        JsonPath updatedRental = updateResponseBody.getBody().jsonPath();
        System.out.println("Cars rented in created tenant = " +updatedRental.getString("rentedCars"));

        assertThat(addResponseBody.getStatusCode()).isEqualTo(201);
        assertThat(updatedRental.getString("rentedCars[0].model")).isEqualTo("Clio");
        assertThat(updatedRental.getString("rentedCars[1].model")).isEqualTo("Mondeo");
    }

    @Test
    public void patchARentalTest() {
        System.out.println("appUrl = " + endpoint);
        Map<String, Object> newRental = new HashMap<>();
        newRental.put("beginningOfRental", LocalDate.now().toString());
        newRental.put("carIds", Arrays.asList(103, 104));
        newRental.put("endOfRental", LocalDate.now().plusDays(10).toString());
        newRental.put("tenantId", 102);

        Response addResponseBody = requestSpecification
                .contentType("application/json")
                .body(newRental)
                .when()
                .post(rentalEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addResponseBody.getStatusCode());
        JsonPath rental = addResponseBody.getBody().jsonPath();
        addedRentalId = Long.parseLong(rental.getString("id"));
        System.out.println("id of created tenant = " +rental.getString("id"));
        System.out.println("Cars rented in created tenant = " +rental.getString("rentedCars"));

        Map<String, Object> patchRental = new HashMap<>();
        patchRental.put("carIds", Arrays.asList(103));
        patchRental.put("tenantId", 102);

        Response patchResponseBody = requestSpecification
                .contentType("application/json")
                .body(patchRental)
                .when()
                .patch(rentalEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +patchResponseBody.getStatusCode());
        JsonPath patchedRental = patchResponseBody.getBody().jsonPath();
        System.out.println("Cars rented in created tenant = " +patchedRental.getString("rentedCars"));

        assertThat(addResponseBody.getStatusCode()).isEqualTo(201);
        ArrayList<String> rentedCars = patchedRental.get("rentedCars");
        assertThat(rentedCars).hasSize(1);
    }


    @Test
    public void deleteARentalByRentalIdTest() {
        System.out.println("appUrl = " + endpoint);
        Map<String, Object> newRental = new HashMap<>();
        newRental.put("beginningOfRental", LocalDate.now().toString());
        newRental.put("carIds", Arrays.asList(103));
        newRental.put("endOfRental", LocalDate.now().plusDays(10).toString());
        newRental.put("tenantId", 102);

        Response addResponseBody = requestSpecification
                .contentType("application/json")
                .body(newRental)
                .when()
                .post(rentalEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addResponseBody.getStatusCode());
        JsonPath rental = addResponseBody.getBody().jsonPath();
        addedRentalId = Long.parseLong(rental.getString("id"));
        System.out.println("id of created tenant = " +rental.getString("id"));
        System.out.println("Cars rented in created tenant = " +rental.getString("rentedCars"));

        Response deletedResponseBody = requestSpecification
                .contentType("application/json")
                .when()
                .delete(rentalEndpoint + "/remove/" + addedRentalId)
                .then()
                .extract().response();
        System.out.println("status = " +deletedResponseBody.getStatusCode());
        assertThat(deletedResponseBody.getStatusCode()).isEqualTo(200);

        Response responseBody = requestSpecification
                .when()
                .get(rentalEndpoint + "/search/" + addedRentalId)
                .then()
                .extract().response();
        System.out.println("status = " + responseBody.getStatusCode());
        assertThat(responseBody.getStatusCode()).isEqualTo(404);

    }

    @Test
    public void deleteARentalByTenantIdTest() {
        System.out.println("appUrl = " + endpoint);
        int tenantId = 102;
        Map<String, Object> newRental = new HashMap<>();
        newRental.put("beginningOfRental", LocalDate.now().toString());
        newRental.put("carIds", Arrays.asList(103));
        newRental.put("endOfRental", LocalDate.now().plusDays(10).toString());
        newRental.put("tenantId", tenantId);

        Response addResponseBody = requestSpecification
                .contentType("application/json")
                .body(newRental)
                .when()
                .post(rentalEndpoint)
                .then()
                .extract().response();
        System.out.println("status = " +addResponseBody.getStatusCode());
        JsonPath rental = addResponseBody.getBody().jsonPath();
        addedRentalId = Long.parseLong(rental.getString("id"));
        System.out.println("id of created tenant = " +rental.getString("id"));
        System.out.println("Cars rented in created tenant = " +rental.getString("rentedCars"));

        Response deletedResponseBody = requestSpecification
                .contentType("application/json")
                .when()
                .delete(rentalEndpoint + "/remove/byTenant/" + tenantId)
                .then()
                .extract().response();
        System.out.println("status = " +deletedResponseBody.getStatusCode());
        assertThat(deletedResponseBody.getStatusCode()).isEqualTo(200);

        Response responseBody = requestSpecification
                .when()
                .get(rentalEndpoint + "/search/" + addedRentalId)
                .then()
                .extract().response();
        System.out.println("status = " + responseBody.getStatusCode());
        assertThat(responseBody.getStatusCode()).isEqualTo(404);

    }

}

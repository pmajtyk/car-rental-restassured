Feature: Checking the correct acting of the Car Rental REST service
  As a user
  I want to validate the execution of the GET, POST, PUT, PATCH and DELETE methods
  By sending queries to the service

  Scenario: Checking the correctness of the GET query
    Given the REST service with initial "car" data id "102" is available and the "GET" method is supported
    When I send request with content type "application/json" to the service
    Then the retrieved body should contains the "brand" "Volkswagen" and the "model" "Golf" and the status code "200"

  @test
  Scenario: Checking the correctness of the POST query
    Given the REST service with initial "car" endpoint is available and the "POST" method is supported
    And input data is taken from "CarAudiA4.json"
    When I send request with content type "application/json" to the service
    Then the retrieved body should contains the "brand" "Audi" and the "model" "A4" and the status code "201"

package com.epam.microservice.steps;

import com.epam.microservice.common.EntityNotFoundException;
import com.epam.microservice.cucumber.CucumberSpringConfiguration;
import com.epam.microservice.dto.ActionType;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.service.TrainerSummariesService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.spring.CucumberContextConfiguration;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@CucumberContextConfiguration
@ExtendWith(MockitoExtension.class)
public class StepDefinitions extends CucumberSpringConfiguration {
    @Value("${microservice.secretKey}")
    private String key;
    private String jwt;
    private ResponseEntity<?> response;
    private SubmitWorkloadChangesRequestBody requestBody;
    @MockBean
    private TrainerSummariesService service;

    @Given("a valid SubmitWorkloadChangesRequestBody")
    public void aValidSubmitWorkloadChangesRequestBody() {
        requestBody = SubmitWorkloadChangesRequestBody.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerIsActive(true)
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(90)
                .changeType(ActionType.ADD)
                .build();
    }

    @Given("an invalid SubmitWorkloadChangesRequestBody")
    public void anInvalidSubmitWorkloadChangesRequestBody() {
        requestBody = SubmitWorkloadChangesRequestBody.builder()
                .trainerUsername("")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerIsActive(true)
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(90)
                .changeType(ActionType.ADD)
                .build();
    }

    @When("I send a PATCH request to {string} {string} exception")
    public void iSendAPATCHRequestTo(String endpoint, String isExpException) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<SubmitWorkloadChangesRequestBody> entity = new HttpEntity<>(requestBody, headers);
        if (isExpException.equals("expecting")) {
            response = testRestTemplate.exchange(endpoint, HttpMethod.PATCH, entity, String.class);
        } else {
            response = testRestTemplate.exchange(endpoint, HttpMethod.PATCH, entity, Void.class);
        }
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        assertEquals(statusCode, response.getStatusCode().value());
    }

    @Given("an existing username {string}")
    public void anExistingUsername(String username) {
        ResponseSummary summary = ResponseSummary.builder()
                .username(username)
                .firstName("John")
                .lastName("Doe")
                .status(true)
                .build();
        when(service.getSummary(username)).thenReturn(summary);
    }

    @When("I send a GET request to {string} {string} exception")
    public void iSendAGETRequestTo(String endpoint, String isExpException) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        if (isExpException.equals("expecting")) {
            response = testRestTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, String.class);
        } else {
            response = testRestTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, ResponseSummary.class);
        }
    }

    @And("the response body should contain a username {string}")
    public void theResponseBodyShouldContainAUsername(String username) {
        ResponseSummary actual = (ResponseSummary) response.getBody();
        assertEquals(username, actual.getUsername());
    }

    @Given("a non-existing username {string}")
    public void aNonExistingUsername(String username) {
        when(service.getSummary(username))
                .thenThrow(new EntityNotFoundException("Trainer summary for username " + username + " was not found"));
    }

    @And("the response body should contain {string}")
    public void theResponseBodyShouldContain(String exceptionMessage) {
        assertTrue(response.getBody().toString().contains(exceptionMessage));
    }

    @And("the service throws an unexpected error")
    public void theServiceThrowsAnUnexpectedError() {
        doThrow(new RuntimeException("Unexpected database error"))
                .when(service).submitWorkloadChanges(Mockito.any());
    }

    @And("a valid JWT")
    public void aValidJWT() {
       jwt = generateJwtToken();
    }

    @And("an invalid JWT")
    public void anInvalidJWT() {
        jwt = "invalidToken";
    }

    private String generateJwtToken() {
        return Jwts.builder()
                .subject("Test token")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(secretKey())
                .compact();
    }

    private SecretKey secretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, "HmacSHA256");
    }
}

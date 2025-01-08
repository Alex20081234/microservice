package com.epam.microservice.steps.integration;

import com.epam.microservice.cucumber.CucumberSpringConfiguration;
import com.epam.microservice.domain.MonthlyWorkload;
import com.epam.microservice.domain.TrainerSummary;
import com.epam.microservice.domain.YearlyWorkload;
import com.epam.microservice.dto.ActionType;
import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.time.Month;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
public class ControllersIntegrationStepDefinitions extends CucumberSpringConfiguration {
    @Autowired
    private MongoTemplate template;
    @Value("${microservice.secretKey}")
    private String key;
    private String jwt;
    private ResponseEntity<?> response;
    private SubmitWorkloadChangesRequestBody body;

    @Given("a valid request body")
    public void givenAValidRequestBody() {
        body = SubmitWorkloadChangesRequestBody.builder()
                .trainerUsername("John.Doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerIsActive(true)
                .trainingDate(LocalDate.of(2025, 1, 10))
                .trainingDurationMinutes(90)
                .changeType(ActionType.ADD)
                .build();
    }

    @Given("a valid JWT")
    public void givenAValidJWT() {
        jwt = generateJwtToken();
    }

    @When("I send a PATCH request to {string} {string} exception")
    public void whenISendAPATCHRequest(String endpoint, String isExpException) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<SubmitWorkloadChangesRequestBody> entity = new HttpEntity<>(body, headers);
        if (isExpException.equals("expecting")) {
            response = testRestTemplate.exchange(endpoint, HttpMethod.PATCH, entity, String.class);
        } else {
            response = testRestTemplate.exchange(endpoint, HttpMethod.PATCH, entity, Void.class);
        }
    }

    @Then("the response status should be {int}")
    public void thenTheResponseStatusShouldBe(int statusCode) {
        assertEquals(statusCode, response.getStatusCode().value());
    }

    @And("db should contain new workload")
    public void andDbShouldContainNewWorkload() {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is("John.Doe"));
        TrainerSummary summary = template.findOne(query, TrainerSummary.class);
        assertEquals("John.Doe", summary.getUsername());
        assertEquals("John", summary.getFirstName());
        assertEquals("Doe", summary.getLastName());
        assertTrue(summary.isStatus());
        MonthlyWorkload monthlyWorkload = summary.getWorkloads().get(0).getList().get(0);
        assertEquals(Month.JANUARY, monthlyWorkload.getMonth());
        assertEquals(90, monthlyWorkload.getWorkingHours());
    }

    @Given("an existing user info")
    public void givenAnExistingUserInfo() {
        MonthlyWorkload m = MonthlyWorkload.builder()
                .month(Month.JANUARY)
                .workingHours(90)
                .build();
        YearlyWorkload y = YearlyWorkload.builder()
                .list(List.of(m))
                .year(2025)
                .build();
        TrainerSummary summary = TrainerSummary.builder()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .status(true)
                .workloads(List.of(y))
                .build();
        template.save(summary);
    }

    @When("I send a GET request to {string} {string} exception")
    public void whenISendAGETRequest(String endpoint, String isExpException) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        if (isExpException.equals("expecting")) {
            response = testRestTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, String.class);
        } else {
            response = testRestTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, ResponseSummary.class);
        }
    }

    @And("the response body should contain correct info")
    public void andTheResponseBodyShouldContainCorrectInfo() {
        ResponseSummary summary = (ResponseSummary) response.getBody();
        assertEquals("John.Doe", summary.getUsername());
        assertEquals("John", summary.getFirstName());
        assertEquals("Doe", summary.getLastName());
        assertTrue(summary.isStatus());
        MonthlyWorkload monthlyWorkload = summary.getList().get(0).getList().get(0);
        assertEquals(Month.JANUARY, monthlyWorkload.getMonth());
        assertEquals(90, monthlyWorkload.getWorkingHours());
    }

    @Given("an invalid request body")
    public void givenAnInvalidRequestBody() {
        body = SubmitWorkloadChangesRequestBody.builder()
                .trainerUsername("")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerIsActive(true)
                .trainingDate(LocalDate.of(2025, 1, 10))
                .trainingDurationMinutes(90)
                .changeType(ActionType.ADD)
                .build();
    }

    @And("db shouldn't contain changes")
    public void andDbShouldntContainChanges() {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is("John.Doe"));
        TrainerSummary summary = template.findOne(query, TrainerSummary.class);
        assertNull(summary);
    }

    @Then("the response body should contain {string}")
    public void thenTheResponseBodyShouldContain(String exceptionMessage) {
        System.out.println(response.getBody().toString());
        assertTrue(response.getBody().toString().contains(exceptionMessage));
    }

    @Given("a non-existing user info")
    public void givenANonExistingUserInfo() {}

    @Given("an invalid JWT")
    public void givenAnInvalidJWT() {
        jwt = "invalid";
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

@component
Feature: Workload Controller API Tests

  # Positive Test Cases
  Scenario: Submit workload changes with a valid request
    Given a valid SubmitWorkloadChangesRequestBody
    And a valid JWT
    When I send a PATCH request to "/api/v1/workload/submit" "not expecting" exception
    Then the response status should be 204

  Scenario: Get summary for an existing user
    Given an existing username "John.Doe"
    And a valid JWT
    When I send a GET request to "/api/v1/workload/summary/John.Doe" "not expecting" exception
    Then the response status should be 200
    And the response body should contain a username "John.Doe"

  # Negative Test Cases
  Scenario: Submit workload changes with an invalid request
    Given an invalid SubmitWorkloadChangesRequestBody
    And a valid JWT
    When I send a PATCH request to "/api/v1/workload/submit" "expecting" exception
    Then the response status should be 400
    And the response body should contain "Validation failed "

  Scenario: Get summary for a non-existing user
    Given a non-existing username "Non.Existent"
    And a valid JWT
    When I send a GET request to "/api/v1/workload/summary/Non.Existent" "expecting" exception
    Then the response status should be 400
    And the response body should contain "Trainer summary for username Non.Existent was not found"

  Scenario: Submit workload changes with an unexpected error
    Given a valid SubmitWorkloadChangesRequestBody
    And a valid JWT
    And the service throws an unexpected error
    When I send a PATCH request to "/api/v1/workload/submit" "expecting" exception
    Then the response status should be 500
    And the response body should contain "Unexpected error occurred : "

  Scenario: Get summary with an invalid JWT
    Given an existing username "John.Doe"
    And an invalid JWT
    When I send a GET request to "/api/v1/workload/summary/John.Doe" "expecting" exception
    Then the response status should be 403
    And the response body should contain "Forbidden"

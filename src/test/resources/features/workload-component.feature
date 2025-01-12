@component
Feature: Workload API Component Tests

  # Positive Test Cases
  Scenario: Submit workload changes with a valid request
    Given a valid request body
    And a valid JWT
    When I send a PATCH request to "/api/v1/workload/submit" "not expecting" exception
    Then the response status should be 204
    And db should contain new workload

  Scenario: Get summary for an existing user
    Given an existing user info
    And a valid JWT
    When I send a GET request to "/api/v1/workload/summary/John.Doe" "not expecting" exception
    Then the response status should be 200
    And the response body should contain correct info

  # Negative Test Cases
  Scenario: Submit workload changes with an invalid request
    Given an invalid request body
    And a valid JWT
    When I send a PATCH request to "/api/v1/workload/submit" "expecting" exception
    Then the response status should be 400
    And the response body should contain "Validation failed "
    And db shouldn't contain changes

  Scenario: Get summary for a non-existing user
    Given a non-existing user info
    And a valid JWT
    When I send a GET request to "/api/v1/workload/summary/Non.Existent" "expecting" exception
    Then the response status should be 400
    And the response body should contain "Trainer summary for username Non.Existent was not found"

  Scenario: Get summary with an invalid JWT
    Given an invalid JWT
    When I send a GET request to "/api/v1/workload/summary/John.Doe" "expecting" exception
    Then the response status should be 403
    And the response body should contain "Forbidden"
    And db shouldn't contain changes

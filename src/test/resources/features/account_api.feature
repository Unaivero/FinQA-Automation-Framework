Feature: Account API Operations
  As a developer integrating with the FinQA banking API
  I want to access account information programmatically
  So that I can build integrations and automated processes

  Background:
    Given the API client is properly authenticated

  Scenario: Retrieve all accounts for a user
    When the client requests all accounts
    Then the response status code should be 200
    And the response should contain a list of accounts
    And each account should have an id, name, and balance

  Scenario: Retrieve specific account details by ID
    When the client requests account with ID "800000"
    Then the response status code should be 200
    And the response should contain account details
    And the account ID should match "800000"

  Scenario: Retrieve account transactions
    When the client requests transactions for account with ID "800000"
    Then the response status code should be 200
    And the response should contain a list of transactions
    And each transaction should have an id, date, amount, and description

  Scenario: Attempt to access account with invalid credentials
    Given the API client is using invalid credentials
    When the client requests all accounts
    Then the response status code should be 401
    And the response should contain an authentication error message

  Scenario: Attempt to access non-existent account
    When the client requests account with ID "999999"
    Then the response status code should be 404
    And the response should contain a "account not found" error message

  Scenario Outline: Filter accounts by type
    When the client requests all accounts with type "<account_type>"
    Then the response status code should be 200
    And all returned accounts should have type "<account_type>"

    Examples:
      | account_type |
      | checking     |
      | savings      |
      | investment   |
      | credit       |

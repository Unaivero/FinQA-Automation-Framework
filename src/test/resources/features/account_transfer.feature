Feature: Account Fund Transfers
  As a user of the FinQA banking application
  I want to be able to transfer funds between accounts
  So that I can manage my finances efficiently

  Background:
    Given the user is logged into the banking application
    And the user navigates to the transfer funds page

  Scenario: Successful fund transfer between user's accounts
    Given the user has sufficient funds in the source account
    When the user selects source account "12345"
    And the user selects destination account "67890"
    And the user enters transfer amount "500.00"
    And the user provides transfer description "Monthly savings transfer"
    And the user submits the transfer
    Then the system should display a confirmation message
    And the transfer should appear in the transaction history
    And the source account balance should be decreased by "500.00"
    And the destination account balance should be increased by "500.00"

  Scenario: Failed transfer due to insufficient funds
    Given the user has insufficient funds in the source account
    When the user selects source account "12345"
    And the user selects destination account "67890"
    And the user enters transfer amount "2000.00"
    And the user provides transfer description "Emergency funds"
    And the user submits the transfer
    Then the system should display an insufficient funds error
    And the account balances should remain unchanged

  Scenario: Failed transfer due to invalid amount format
    Given the user has sufficient funds in the source account
    When the user selects source account "12345"
    And the user selects destination account "67890"
    And the user enters transfer amount "abc"
    And the user provides transfer description "Test invalid amount"
    And the user submits the transfer
    Then the system should display an invalid amount format error
    And the account balances should remain unchanged

  Scenario Outline: Various transfer scenarios
    Given the user has sufficient funds in the source account
    When the user selects source account "<source_account>"
    And the user selects destination account "<destination_account>"
    And the user enters transfer amount "<amount>"
    And the user provides transfer description "<description>"
    And the user submits the transfer
    Then the system should display a confirmation message
    And the source account balance should be decreased by "<amount>"
    And the destination account balance should be increased by "<amount>"

    Examples:
      | source_account | destination_account | amount    | description          |
      | 12345          | 67890               | 100.00    | Small transfer       |
      | 12345          | 67890               | 1000.00   | Medium transfer      |
      | 12345          | 67890               | 4999.99   | Large transfer       |
      | 67890          | 12345               | 50.00     | Return funds         |

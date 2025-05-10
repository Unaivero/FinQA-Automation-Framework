Feature: User Authentication
  As a user of the FinQA banking application
  I want to be able to log in with my credentials
  So that I can access my account securely

  Background:
    Given the user is on the login page

  Scenario: Successful login with valid credentials
    When the user enters valid username "standard_user"
    And the user enters valid password "secure_password"
    And the user clicks the login button
    Then the user should be redirected to the dashboard
    And the user should see their account summary

  Scenario: Failed login with invalid username
    When the user enters invalid username "wrong_user"
    And the user enters valid password "secure_password"
    And the user clicks the login button
    Then the user should see an error message
    And the user should remain on the login page

  Scenario: Failed login with invalid password
    When the user enters valid username "standard_user"
    And the user enters invalid password "wrong_password"
    And the user clicks the login button
    Then the user should see an error message
    And the user should remain on the login page

  Scenario Outline: Various login scenarios
    When the user enters username "<username>"
    And the user enters password "<password>"
    And the user clicks the login button
    Then the system should respond with "<result>"

    Examples:
      | username      | password        | result                |
      | standard_user | secure_password | successful login      |
      | admin_user    | admin_password  | successful admin login|
      | standard_user | wrong_password  | authentication failed |
      | wrong_user    | secure_password | authentication failed |
      |               | secure_password | username cannot be empty |
      | standard_user |                 | password cannot be empty |

Feature: Visual Validation of UI Elements
  As a quality assurance engineer
  I want to validate the visual appearance of application screens
  So that I can ensure UI consistency and detect unexpected visual changes

  Background:
    Given the visual validation system is initialized

  Scenario: Validate login page appearance
    Given the user is on the login page
    When a screenshot is taken of the login page
    Then the screenshot should match the baseline image

  Scenario: Validate dashboard appearance after login
    Given the user is logged into the banking application
    When a screenshot is taken of the dashboard page
    Then the screenshot should match the baseline image

  Scenario: Validate transfer funds page appearance
    Given the user is logged into the banking application
    And the user navigates to the transfer funds page
    When a screenshot is taken of the transfer funds page
    Then the screenshot should match the baseline image

  Scenario: Validate error message appearance with invalid login
    Given the user is on the login page
    When the user enters invalid username "wrong_user"
    And the user enters invalid password "wrong_password"
    And the user clicks the login button
    And a screenshot is taken of the error message
    Then the screenshot should match the baseline image

  Scenario: Create baseline images if they don't exist
    Given the user is on the login page
    When baseline images don't exist
    And a screenshot is taken of the login page
    Then the system should create a new baseline image
    And report success for the visual comparison

  Scenario Outline: Validate responsive layout at different screen sizes
    Given the browser window is resized to "<width>x<height>"
    And the user is on the login page
    When a screenshot is taken of the login page with name "login_<width>x<height>"
    Then the screenshot should match the baseline image

    Examples:
      | width | height |
      | 1920  | 1080   |
      | 1366  | 768    |
      | 1024  | 768    |
      | 768   | 1024   |
      | 375   | 812    |

Feature: Login Functionality

@smoke
  Scenario: Login with valid credentials
    Given Navigate to the website
    And Enter a valid username and password
    Then Click on the login button

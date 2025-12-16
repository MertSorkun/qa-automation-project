Feature: Login Functionality

  @smoke
  Scenario Outline: Login with different credential combinations
    Given I open the login page
    When I login with "<username>" and "<password>"
    Then I should see "<result>"
    And the error message should be "<error>"

    Examples:
      | username       | password       | result  | error                                                                 |
      | standard_user  | secret_sauce   | success |                                                                      |
      |                | secret_sauce   | error   | Epic sadface: Username is required                                    |
      | standard_user  |                | error   | Epic sadface: Password is required                                    |
      | invalid_user   | wrong_pass     | error   | Epic sadface: Username and password do not match any user in this service |
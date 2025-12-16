Feature: Logout Functionality

  @smoke
  Scenario: Logout after successful login
    Given I open the login page
    When I login with "standard_user" and "secret_sauce"
    And I logout
    Then I should see the login page
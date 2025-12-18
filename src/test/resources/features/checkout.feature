Feature: Checkout Flow

  @checkout
  Scenario: Complete checkout end-to-end
    Given I open the login page
    When I login with "standard_user" and "secret_sauce"
    And I add a random set of 2 or 3 items to the cart
    And I proceed to checkout and complete the order
    Then the order should be completed successfully
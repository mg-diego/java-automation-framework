@Chrome
Feature: Login

  Scenario: Login
    Given the user enters the username 'standard_user'
    And the user enters password 'secret_sauce'
    When the user clicks on submit button
    Then the user can login

  Scenario: Logout
    Given the user enters the username 'standard_user'
    And the user enters password 'secret_sauce'
    When the user clicks on submit button
    Then the user can login
    When the user opens menu
    And the user clicks on Logout button
    Then the user is at homepage
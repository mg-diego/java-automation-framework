@Web @Parallel
Feature: Login

  Cucumber web feature example using the SwagLabs page
  with normal scenarios and also scenarios outline.


  Scenario: TEST-01 - [CHROME - vMAXIMUM_VERSION - 1920x1080] Login
    Given the user enters the username 'standard_user'
    And the user enters password 'secret_sauce'
    When the user clicks on submit button
    Then the user can login

  Scenario: TEST-02 - [FIREFOX - vMAXIMUM_VERSION - 1920x1080] Logout
    Given the user enters the username 'standard_user'
    And the user enters password 'secret_sauce'
    When the user clicks on submit button
    Then the user can login
    When the user opens menu
    And the user clicks on Logout button
    Then the user is at homepage

  Scenario Outline: <TestId> [<Browser> - v<Version> - <Resolution>] Wrong credentials
    Given the user enters the username '<Username>'
    And the user enters password '<Password>'
    When the user clicks on submit button
    Then the login error message appears

    Examples:
      | TestId  | Browser | Version         | Resolution | Username      | Password     |
      | TEST-03 | CHROME  | MAXIMUM_VERSION | 1920x1080  | error         | secret_sauce |
      | TEST-04 | FIREFOX | MINIMUM_VERSION | 1920x1080  | standard_user | error        |
      | TEST-05 | EDGE    | MAXIMUM_VERSION | 1280x768   | error         | error        |
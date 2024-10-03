@API @Parallel
Feature: Gender by Name


    Scenario: TEST_06 - GET - Gender by Name
      When the GET gender by name endpoint is requested with 'Diego' name
      Then the response message status code is '200'
      And the response message contains '"gender":"male"'
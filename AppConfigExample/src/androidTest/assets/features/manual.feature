@reset @all @manual
Feature: I can manually change the active configuration
  As an app developer or tester
  I want to be able to manually change the active configuration
  So I can optimize testing and make smaller test scripts

  Scenario: Manually change a configuration
    Given I am on the "App configurations" page
    When I reset configuration data
    And I select the "Test server" configuration
    And I manually change "apiUrl" into "https://manualchange.example.com/"
    Then I see "apiUrl" set to "https://manualchange.example.com/"

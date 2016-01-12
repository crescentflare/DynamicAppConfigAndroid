@reset @all @main
Feature: I can change my application configuration
  As an app developer or tester
  I want to be able to change the configuration
  So I can test multiple configurations in one build

  Scenario: Selecting a configuration
    Given I am on the "App configurations" page
    When I select the "Test server" configuration
    Then I see the "Test server" settings

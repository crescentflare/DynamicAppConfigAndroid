@reset @all @edit
Feature: I can edit an application configuration
  As an app developer or tester
  I want to be able to edit a specific configuration
  So I can further customize app behavior during testing

  Scenario: Edit a configuration
    Given I am on the "App configurations" page
    When I reset configuration data
    And I edit the "Test server" configuration
    Then I see the "Edit configuration" page
    When I change "apiUrl" into string "https://changed.example.com/"
    And I change "networkTimeoutSec" into number "10"
    And I apply the changes
    And I select the "Test server" configuration
    Then I see "apiUrl" set to "https://changed.example.com/"
    And I see "networkTimeoutSec" set to "10"

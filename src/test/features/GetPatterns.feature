Feature: Get Patterns

  Scenario Outline: User calls web service to get a patterns
    Given get patterns api
    When a user retrieves the patterns
    Then verify successful request
    And the number of views for each pattern in response should be greater than <numViewsMin>

    Examples: 
      | numViewsMin |
      |        4000 |

Feature: Charts API

  Background:
    * url baseUrl

  Scenario: Fetch available charts

    Given path '/charts'
    When method GET
    Then status 200
    And assert response.length == 2
    And match response[0].id == 'bitnami/wordpress'
    And match response[1].id == 'bitnami/drupal'

@trainings @actions
Feature: Training actions

  Background:
    Given the user is logged in to MatrixERP
    And the user opens the "trainings" page

  Scenario: Each row offers view, edit and delete
    Then the first row offers the actions "Bax,Düzəliş et,Sil"

  Scenario Outline: Row offers the action
    Then the first row offers the action "<action>"

    Examples:
      | action      |
      | Bax         |
      | Düzəliş et  |
      | Sil         |
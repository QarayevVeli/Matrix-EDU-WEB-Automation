@menu
Feature: Education menu

  Background:
    Given the user is logged in to MatrixERP

  Scenario: Education menu lists all pages
    Then the Education menu contains "Təlimlər,Müəllimlər,Cədvəl"

  Scenario Outline: Education page opens the expected address from the menu
    When the user opens the "<page>" page from the Education menu
    Then the current page url contains "<path>"

    Examples:
      | page      | path                 |
      | trainings | /education/trainings |
      | teachers  | /education/teachers  |
      | schedule  | /education/schedule  |

  Scenario Outline: Page opened from the menu is not the not found page
    When the user opens the "<page>" page from the Education menu
    Then the page is not the not found page

    Examples:
      | page      |
      | trainings |
      | teachers  |
      | schedule  |

  Scenario Outline: Page opened by its address is not the not found page
    When the user opens the address "<path>"
    Then the page is not the not found page

    Examples:
      | path                 |
      | /education/trainings |
      | /education/teachers  |
      | /education/schedule  |

  Scenario: Unknown education address shows the not found page
    When the user opens the address "/education/unknown-page"
    Then the not found page is displayed

  Scenario: Not found page offers the link back to the home page
    When the user opens the address "/education/unknown-page"
    Then the not found page offers the link back to the home page

  Scenario: Link on the not found page leads to the dashboard
    When the user opens the address "/education/unknown-page"
    And the user clicks the link back to the home page
    Then the current page url contains "/dashboard"

  Scenario: Back navigation from the not found page returns to the previous page
    Given the user opens the "trainings" page
    When the user opens the address "/education/unknown-page"
    And the user navigates back in the browser
    Then the current page url contains "/education/trainings"
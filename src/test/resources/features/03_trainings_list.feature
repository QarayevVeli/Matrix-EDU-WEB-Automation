@trainings @list
Feature: Trainings list

  Background:
    Given the user is logged in to MatrixERP
    And the user opens the "trainings" page

  Scenario: Grid shows the expected columns
    Then the grid columns start with "#,Ad,Təsvir,Əməliyyatlar"

  Scenario: Results info shows the range and the total
    Then the results info has the format of a range and a total

  Scenario: Every training fits on the first page
    Then the grid lists 10 rows on the first page

  Scenario: Excel export button is available
    Then the export button is displayed

  Scenario: Excel export does not cause an error
    When the user exports the grid to Excel
    Then no error popup is displayed

  Scenario Outline: Name filter finds matching trainings
    When the user filters the "Ad" column by "<text>"
    Then the grid shows at least 1 record
    And every row contains "<text>" in the "Ad" column ignoring case

    Examples:
      | text   |
      | c#     |
      | C#     |
      | ascasc |
      | ASCASC |
      | qwerf  |
      | sql    |
      | qa     |
      | samir  |
      | sam    |

  Scenario: Upper case filter text with the letter I finds matching trainings
    When the user filters the "Ad" column by "SAMIR"
    Then the grid shows at least 1 record

  Scenario: Filter text with surrounding spaces still finds the training
    When the user filters the "Ad" column by "  c#  "
    Then the grid shows at least 1 record

  Scenario Outline: Name filter without matches shows an empty grid
    When the user filters the "Ad" column by "<text>"
    Then the grid shows no records

    Examples:
      | text     |
      | zzzzqqqq |
      | %        |
      | _        |
      | '        |

  Scenario: Very long filter text shows an empty grid
    When the user filters the "Ad" column by a text of 300 characters
    Then the grid shows no records

  Scenario Outline: Special characters in the filter do not cause a server error
    When the user filters the "Ad" column by "<text>"
    Then no error popup is displayed

    Examples:
      | text     |
      | <script> |
      | a,b      |
      | a\|b     |
      | (a)      |

  Scenario Outline: Description filter finds matching trainings
    When the user filters the "Təsvir" column by "<text>"
    Then the grid shows at least 1 record
    And every row contains "<text>" in the "Təsvir" column ignoring case

    Examples:
      | text |
      | qüer    |
      | QÜER    |
      | üef     |
      | daf     |
      | qasdfrr |

  Scenario: Description filter without matches shows an empty grid
    When the user filters the "Təsvir" column by "zzzzqqqq"
    Then the grid shows no records

  Scenario: Name and description filters work together
    When the user filters the "Ad" column by "qa"
    And the user filters the "Təsvir" column by "üef"
    Then the grid shows at least 1 record

  Scenario: Sorting by name ascending
    When the user sorts by the "Ad" column
    Then the "Ad" column is sorted in ascending order
    And the "Ad" column header shows a sort number

  Scenario: Sorting by name descending
    When the user sorts by the "Ad" column
    And the user sorts by the "Ad" column
    Then the "Ad" column is sorted in descending order

  Scenario: Sorting by number ascending
    When the user sorts by the "#" column
    Then the "#" column is sorted in ascending order

  Scenario: Sorting by number descending
    When the user sorts by the "#" column
    And the user sorts by the "#" column
    Then the "#" column is sorted in descending order

  Scenario: Sorting keeps the applied filter
    When the user filters the "Ad" column by "qa"
    And the user sorts by the "Ad" column
    Then every row contains "qa" in the "Ad" column ignoring case

  Scenario: Next page button is disabled when all records fit on one page
    Then the next page button is disabled

  Scenario: Previous page button is disabled on the first page
    Then the previous page button is disabled

  Scenario: Page size offers the expected options
    Then the page size options are "10,25,50,100"

  Scenario Outline: Page size limits the number of rows
    When the user selects the page size <size>
    Then the grid shows no more than <size> rows and the selected page size is <size>

    Examples:
      | size |
      | 10   |
      | 25   |
      | 50   |
      | 100  |

  Scenario: Refresh keeps the applied filter
    When the user filters the "Ad" column by "qa"
    And the user refreshes the grid
    Then the "Ad" filter still contains "qa"

  Scenario Outline: Column header is displayed
    Then the grid columns start with "<column>"

    Examples:
      | column      |
      | #           |
      | Ad          |
      | Təsvir      |
      | Əməliyyatlar |

  Scenario: Refresh keeps the selected page size
    When the user selects the page size 25
    And the user refreshes the grid
    Then the grid shows no more than 25 rows and the selected page size is 25

  Scenario: Reset clears the filter and restores all records
    When the user notes the total number of records
    And the user filters the "Ad" column by "qa"
    And the user resets the filters
    Then the "Ad" filter is empty
    And the total number of records equals the noted number
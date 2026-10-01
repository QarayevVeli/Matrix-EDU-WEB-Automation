@trainings @create
Feature: Create training

  Background:
    Given the user is logged in to MatrixERP
    And the user opens the "trainings" page
    And the user opens the new training form

  Scenario: New training form shows the title and the labels
    Then the training form title is "Yeni Təlim"
    And the training form has the labels "Ad,Təsvir"

  Scenario: New training form offers the add and cancel buttons
    Then the training form has the buttons "Əlavə et,Ləğv et"

  Scenario: Name field limits the input to 150 characters
    When the user enters a training name of 151 characters
    Then the training name field keeps at most 150 characters

  Scenario: Description field limits the input to 500 characters
    When the user enters a description of 501 characters
    Then the training description field keeps at most 500 characters

  Scenario: Empty name is rejected
    When the user submits the training form
    Then the training form shows the validation message "Bu sahə məcburidir!"
    And the training form is still open

  Scenario: Description without a name is rejected
    When the user enters the description "Yalnız təsvir"
    And the user submits the training form
    Then the training form shows the validation message "Bu sahə məcburidir!"

  Scenario Outline: Name with only spaces is rejected
    When the user enters <count> spaces as the training name
    And the user submits the training form
    Then an error popup with message "Təlim adı boş ola bilməz" is displayed

    Examples:
      | count |
      | 1     |
      | 5     |
      | 50    |

  Scenario: Valid training with a name only is created
    When the user enters a unique training name
    And the user submits the training form
    Then a success popup with message "Uğurla əlavə edildi." is displayed
    When the user closes the popup
    Then the training is listed in the grid

  Scenario: Valid training with a name and a description is created
    When the user enters a unique training name
    And the user enters the description "Test təsviri"
    And the user submits the training form
    Then a success popup with message "Uğurla əlavə edildi." is displayed
    When the user closes the popup
    Then the training is listed in the grid with the recorded description

  Scenario: Training with a name of 150 characters is created
    When the user enters a unique training name of 150 characters
    And the user submits the training form
    Then a success popup with message "Uğurla əlavə edildi." is displayed
    When the user closes the popup
    Then the training is listed in the grid

  Scenario: Training with a description of 500 characters is created
    When the user enters a unique training name
    And the user enters a description of 500 characters
    And the user submits the training form
    Then a success popup with message "Uğurla əlavə edildi." is displayed
    When the user closes the popup
    Then the training is listed in the grid with the recorded description

  Scenario Outline: Training name with special characters is created as plain text
    When the user enters a unique training name containing "<text>"
    And the user submits the training form
    Then a success popup with message "Uğurla əlavə edildi." is displayed
    When the user closes the popup
    Then the training is listed in the grid

    Examples:
      | text                        |
      | Ümid & Şirkət               |
      | <b>bold</b>                 |
      | '; DROP TABLE Trainings;--  |
      | Тренинг                     |
      | İşçi təlimi (Java)          |
      | 100%                        |
      | a/b\\c                      |
      | &lt;i&gt;                   |
      | null                        |

  Scenario Outline: Training name variants are created
    When the user enters a unique training name containing "<text>"
    And the user submits the training form
    Then a success popup with message "Uğurla əlavə edildi." is displayed
    When the user closes the popup
    Then the training is listed in the grid

    Examples:
      | text         |
      | 0            |
      | 123456       |
      | Test Təlimi  |
      | UPPERCASE    |
      | lowercase    |
      | Mixed Case-1 |

  Scenario: Enter key in the name field submits the form
    When the user enters a unique training name
    And the user presses Enter in the training name field
    Then a success popup with message "Uğurla əlavə edildi." is displayed

  Scenario: Duplicate training name is rejected
    When the user enters a unique training name
    And the user submits the training form
    And the user closes the popup
    And the user opens the new training form again and enters the name of the existing training
    And the user submits the training form
    Then no success popup is displayed
    And the training is listed in the grid exactly 1 time

  Scenario: Cancel closes the form without creating a training
    When the user enters a unique training name
    And the user cancels the training form
    Then the training form is closed
    And the training is not listed in the grid

  Scenario: Close button closes the form
    When the user closes the training form with the close button
    Then the training form is closed

  Scenario: Created training is shown first in the grid
    When the user enters a unique training name
    And the user submits the training form
    And the user closes the popup
    Then the newest training is shown in the first row

  Scenario: Created training increases the total number of records
    When the user cancels the training form
    And the user notes the total number of records
    And the user creates a training with a unique name
    Then the total number of records is one more than the noted number
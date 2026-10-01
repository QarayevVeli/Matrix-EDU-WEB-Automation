@login
Feature: Login

  Background:
    Given the login page is opened

  Scenario: Successful login opens the dashboard
    When the user logs in with the configured credentials
    Then the dashboard is displayed

  Scenario: Login page offers the forgot password link
    Then the forgot password link is displayed

  Scenario: Wrong password is rejected
    When the user logs in with the configured username and password "YanlisSifre#987"
    Then the user stays on the login page

  Scenario: Empty form is rejected
    When the user submits the login form without credentials
    Then the user stays on the login page

  Scenario: Empty username is rejected
    When the user logs in with an empty username and the configured password
    Then the user stays on the login page

  Scenario: Empty password is rejected
    When the user logs in with the configured username and an empty password
    Then the user stays on the login page

  Scenario: Unknown user is rejected
    When the user logs in with username "yoxdur.istifadeci@example.com" and password "Test#12345"
    Then the user stays on the login page

  Scenario Outline: Dangerous username text does not grant access
    When the user logs in with username "<username>" and password "Test#12345"
    Then the user stays on the login page

    Examples:
      | username                       |
      | <script>alert(1)</script>      |
      | ' OR '1'='1                    |
      | admin'--                       |
      | <img src=x onerror=alert(1)>   |

  Scenario: Dangerous password text does not grant access
    When the user logs in with the configured username and password "<script>alert(1)</script>"
    Then the user stays on the login page

  Scenario: Very long username is rejected
    When the user logs in with a username of 300 characters and password "Test#12345"
    Then the user stays on the login page

  Scenario: Enter key submits the login form
    When the user enters the configured credentials and presses Enter
    Then the dashboard is displayed

  Scenario Outline: Protected page without a session redirects to the login page
    When the user opens "<path>" without a session
    Then the user is redirected to the login page

    Examples:
      | path             |
      | /education/trainings |
      | /education/teachers  |
      | /education/schedule  |

  Scenario: Page refresh keeps the session
    When the user logs in with the configured credentials
    Then the dashboard is displayed
    When the user refreshes the browser
    Then the user is still signed in

  Scenario: Back and forward navigation keep the session
    When the user logs in with the configured credentials
    Then the dashboard is displayed
    When the user opens the "trainings" page
    And the user navigates back in the browser
    And the user navigates forward in the browser
    Then the user is still signed in
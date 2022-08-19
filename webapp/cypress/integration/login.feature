Feature: Login scenarios

  Background: I am on the "Login" Page
    Given I am on the site homepage
    When I click on "Login" button
    Then I am redirected to "/login" Page

  Scenario: I am logged in when I enter correct email and password credentials
    Given I enter "admin@admin.pl" into "email" field
    And I enter "admin" into "password" field
    When I click on "Login" button
    Then I am redirected to "/panel/history" Page
    And Alert "Login successful" is displayed

  Scenario: Alert is displayed when I did not enter email and password credentials
    When I click on "Login" button
    Then Alert "Email or password is invalid" is displayed

  Scenario: Alert is displayed when I did not enter email credential
    Given I enter "admin" into "password" field
    When I click on "Login" button
    Then Alert "Email or password is invalid" is displayed

  Scenario: Alert is displayed when I did not enter password credential
    Given I enter "admin@admin.pl" into "email" field
    When I click on "Login" button
    Then Alert "Email or password is invalid" is displayed

  Scenario: Alert is displayed when I enter incorrect email and password credentials
    Given I enter "test@test.pl" into "email" field
    And I enter "test" into "password" field
    When I click on "Login" button
    Then Alert "Email or password is invalid" is displayed

  Scenario: Alert is displayed when I enter incorrect email and correct password credentials
    Given I enter "adminzzz@admin.pl" into "email" field
    And I enter "admin" into "password" field
    When I click on "Login" button
    Then Alert "Email or password is invalid" is displayed

  Scenario: Alert is displayed when I enter correct email and incorrect password credentials
    Given I enter "admin@admin.pl" into "email" field
    And I enter "adminzzz" into "password" field
    When I click on "Login" button
    Then Alert "Email or password is invalid" is displayed

  Scenario: I am redirected to Register Page
    When I click on "Register instead" button
    Then I am redirected to "/register" Page

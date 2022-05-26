Feature: Login scenarios

  Background: I am on the "Register" Page
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    Then I am redirected to "Login" Page

  Scenario: I am logged in when I enter correct email and password credentials
    Given I enter "admin@admin.pl" into "Email" field on the "Login" Page
    And I enter "admin" into "Password" field on the "Login" Page
    When I click on "Login" button on the "Login" Page
    Then I am redirected to "History" Page of "admin@admin.pl" User
    And Alert "Login successful" is displayed on "History" Page of "admin@admin.pl" User

  Scenario: Alert is displayed when I did not enter email and password credentials
    Given I left empty "Email" field on the "Login" Page
    And I left empty "Password" field on the "Login" Page
    When I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" is displayed on "Login" Page

  Scenario: Alert is displayed when I did not enter email credential
    Given I left empty "Email" field on the "Login" Page
    And I enter "admin" into "Password" field on the "Login" Page
    When I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" is displayed on "Login" Page

  Scenario: Alert is displayed when I did not enter password credential
    Given I enter "admin@admin.pl" into "Email" field on the "Login" Page
    And I left empty "Password" field on the "Login" Page
    When I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" is displayed on "Login" Page

  Scenario: Alert is displayed when I enter incorrect email and password credentials
    Given I enter "test@test.pl" into "Email" field on the "Login" Page
    And I enter "test" into "Password" field on the "Login" Page
    When I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" is displayed on "Login" Page

  Scenario: Alert is displayed when I enter incorrect email and correct password credentials
    Given I enter "adminzzz@admin.pl" into "Email" field on the "Login" Page
    And I enter "admin" into "Password" field on the "Login" Page
    When I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" is displayed on "Login" Page

  Scenario: Alert is displayed when I enter correct email and incorrect password credentials
    Given I enter "admin@admin.pl" into "Email" field on the "Login" Page
    And I enter "adminzzz" into "Password" field on the "Login" Page
    When I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" is displayed on "Login" Page

  Scenario: I am redirected to Register Page
    When I click on "Register instead" button on "Login" Page
    Then I am redirected to "Register" Page

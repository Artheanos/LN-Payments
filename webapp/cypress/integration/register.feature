Feature: Register scenarios

  Background: I am on the "Register" Page
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    Then I am redirected to "Register" Page

  Scenario: I am registered when I fill form correctly
    Given I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "You have been successfully registered. You can now log in!" is displayed on "Register" Page
    And I am redirected to "Login" Page after click "Ok" button from alert

  Scenario: Alert is displayed when user with that email already exist
    Given I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Given email is already in use" is displayed under "Email" field on the "Register" Page

  Scenario: Alert is displayed when I enter not valid email
    Given I enter "test123.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Email not valid!" is displayed under "Email" field on the "Register" Page

  Scenario: Alert is displayed when I left email form empty
    Given I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Email is required!" is displayed under "Email" field on the "Register" Page

  Scenario: Alert is displayed when I left full name form empty
    Given I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Full name is required!" is displayed under "Full name" field on the "Register" Page

  @ignore
  Scenario: Alert is displayed when I enter not valid full name
    Given I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "NaMe@123&" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Full name not valid!" is displayed under "Full name" field on the "Register" Page

  Scenario: Alerts is displayed when I left password form empty
    Given I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Password is required!" is displayed under "Password" field on the "Register" Page
    And Alert "Passwords does not match!" is displayed under "Repeat your password" field on the "Register" Page

  Scenario: Alerts is displayed when I left password and repeat your password forms empty
    Given I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Password is required!" is displayed under "Password" field on the "Register" Page
    And Alert "Passwords does not match!" is displayed under "Repeat your password" field on the "Register" Page

  Scenario: Alert is displayed when I left repeat your password form empty
    Given I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Passwords does not match!" is displayed under "Repeat your password" field on the "Register" Page

  Scenario: Alert is displayed when I enter not valid password
    Given I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "password" into "Password" field on the "Register" Page
    And I enter "password" into "Repeat your password" field on the "Register" Page
    When I click on "Register" button on the "Register" Page
    Then Alert "Password must contain 8 characters, one uppercase, one lowercase, one number and one special case character!" is displayed under "Password" field on the "Register" Page

  Scenario: I am redirected to Login Page
    When I click on "Login instead" button on "Register" Page
    Then I am redirected to "Login" Page

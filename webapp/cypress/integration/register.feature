Feature: Register scenarios

  Background: I am on the "Register" Page
    Given I am on the site homepage
    When I click on "Register" button
    Then I am redirected to "/register" Page

  Scenario: I am registered when I fill form correctly
    Given I enter "test@test.pl" into "email" field
    And I enter "Test User" into "fullName" field
    And I enter "Pa$$word123@" into "password" field
    And I enter "Pa$$word123@" into "passwordConfirmation" field
    When I click on "Register" button
    Then Alert "You have been successfully registered. You can now log in!" is displayed
    And I am redirected to "/login" Page after click "Ok" button from alert

  Scenario: Alert is displayed when user with that email already exist
    Given I enter "test@test.pl" into "email" field
    And I enter "Test User" into "fullName" field
    And I enter "Pa$$word123@" into "password" field
    And I enter "Pa$$word123@" into "passwordConfirmation" field
    When I click on "Register" button
    Then Alert "Given email is already in use" is displayed under "email" field

  Scenario: Alert is displayed when I enter not valid email
    Given I enter "test123.pl" into "email" field
    And I enter "Test User" into "fullName" field
    And I enter "Pa$$word123@" into "password" field
    And I enter "Pa$$word123@" into "passwordConfirmation" field
    When I click on "Register" button
    Then Alert "Email not valid!" is displayed under "email" field

  Scenario: Alert is displayed when I left email form empty
    Given I enter "Test User" into "fullName" field
    And I enter "Pa$$word123@" into "password" field
    And I enter "Pa$$word123@" into "passwordConfirmation" field
    When I click on "Register" button
    Then Alert "Email is required!" is displayed under "email" field

  Scenario: Alert is displayed when I left full name form empty
    Given I enter "test@test.pl" into "email" field
    And I enter "Pa$$word123@" into "password" field
    And I enter "Pa$$word123@" into "passwordConfirmation" field
    When I click on "Register" button
    Then Alert "Full name is required!" is displayed under "fullName" field

  Scenario: Alert is displayed when I enter not valid full name
    Given I enter "test@test.pl" into "email" field
    And I enter "NaMe@123&" into "fullName" field
    And I enter "Pa$$word123@" into "password" field
    And I enter "Pa$$word123@" into "passwordConfirmation" field
    When I click on "Register" button
    Then Alert "Full name cannot contain any special characters" is displayed under "fullName" field

  Scenario: Alerts is displayed when I left password form empty
    Given I enter "test@test.pl" into "email" field
    And I enter "Test User" into "fullName" field
    And I enter "Pa$$word123@" into "passwordConfirmation" field
    When I click on "Register" button
    Then Alert "Password is required!" is displayed under "password" field
    And Alert "Passwords does not match!" is displayed under "passwordConfirmation" field

  Scenario: Alerts is displayed when I left password and repeat your password forms empty
    Given I enter "test@test.pl" into "email" field
    And I enter "Test User" into "fullName" field
    When I click on "Register" button
    Then Alert "Password is required!" is displayed under "password" field
    And Alert "Passwords does not match!" is displayed under "passwordConfirmation" field

  Scenario: Alert is displayed when I left repeat your password form empty
    Given I enter "test@test.pl" into "email" field
    And I enter "Test User" into "fullName" field
    And I enter "Pa$$word123@" into "password" field
    When I click on "Register" button
    Then Alert "Passwords does not match!" is displayed under "passwordConfirmation" field

  Scenario: Alert is displayed when I enter not valid password
    Given I enter "test@test.pl" into "email" field
    And I enter "Test User" into "fullName" field
    And I enter "password" into "password" field
    And I enter "password" into "passwordConfirmation" field
    When I click on "Register" button
    Then Alert "Password must contain 8 characters, one uppercase, one lowercase, one number and one special case character!" is displayed under "password" field

  Scenario: I am redirected to Login Page
    When I click on "Login instead" button
    Then I am redirected to "/login" Page

Feature: Tests for Login Page
  Scenario: I should be logged in when I enter correct email and password credentials
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    And I enter "admin@admin.pl" into "Email" field on the "Login" Page
    And I enter "admin" into "Password" field on the "Login" Page
    And I click on "Login" button on the "Login" Page
    Then I should be reddirected to "History" Page of "admin@admin.pl" User
    And Alert "Login successful" should be displayed on "History" Page of "admin@admin.pl" User

  Scenario: Alert should be displayed when I did not enter email and password credentials
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    And I left empty "Email" field on the "Login" Page
    And I left empty "Password" field on the "Login" Page
    And I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" should be displayed on "Login" Page

  Scenario: Alert should be displayed when I did not enter email credential
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    And I left empty "Email" field on the "Login" Page
    And I enter "admin" into "Password" field on the "Login" Page
    And I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" should be displayed on "Login" Page

  Scenario: Alert should be displayed when I did not enter password credential
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    And I enter "admin@admin.pl" into "Email" field on the "Login" Page
    And I left empty "Password" field on the "Login" Page
    And I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" should be displayed on "Login" Page

  Scenario: Alert should be displayed when I enter incorrect email and password credentials
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    And I enter "test@test.pl" into "Email" field on the "Login" Page
    And I enter "test" into "Password" field on the "Login" Page
    And I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" should be displayed on "Login" Page

  Scenario: Alert should be displayed when I enter incorrect email and correct password credentials
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    And I enter "adminzzz@admin.pl" into "Email" field on the "Login" Page
    And I enter "admin" into "Password" field on the "Login" Page
    And I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" should be displayed on "Login" Page

  Scenario: Alert should be displayed when I enter correct email and incorrect password credentials
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    And I enter "admin@admin.pl" into "Email" field on the "Login" Page
    And I enter "adminzzz" into "Password" field on the "Login" Page
    And I click on "Login" button on the "Login" Page
    Then Alert "Email or password is invalid" should be displayed on "Login" Page

  Scenario: I should be redirected to Register Page
    Given I am on the site homepage
    When I click on "Login" button on the "Home" Page
    And I click on "Register instead" button on "Login" Page
    Then I should be reddirected to "Register" Page

Feature: Tests for Register Page
  Scenario: I should be registered when I fill form correctly
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "You have been successfully registered. You can now log in!" should be displayed on "Register" Page
    And I should be reddirected to "Login" Page after click "Ok" button from alert

  Scenario: Alert should be displayed when user with that email already exist
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I enter "admin@admin.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "Given email is already in use" should be displayed under "Email" field on the "Register" Page

  Scenario: Alert should be displayed when I enter not valid email
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I enter "test123.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "Email not valid!" should be displayed under "Email" field on the "Register" Page

  Scenario: Alert should be displayed when I left email form empty
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I left empty "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "Email is required!" should be displayed under "Email" field on the "Register" Page

  Scenario: Alert should be displayed when I left full name form empty
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I enter "test@test.pl" into "Email" field on the "Register" Page
    And I left empty "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "Full name is required!" should be displayed under "Full name" field on the "Register" Page

    #zapytać sie o walidacje full name, bo mozna wpisac np Name@123

  Scenario: Alerts should be displayed when I left password form empty
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I left empty "Password" field on the "Register" Page
    And I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "Password is required!" should be displayed under "Password" field on the "Register" Page
    And Alert "Passwords does not match!" should be displayed under "Repeat your password" field on the "Register" Page

  Scenario: Alerts should be displayed when I left password and repeat your password forms empty
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I left empty "Password" field on the "Register" Page
    And I left empty "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "Password is required!" should be displayed under "Password" field on the "Register" Page
    And Alert "Passwords does not match!" should be displayed under "Repeat your password" field on the "Register" Page

  Scenario: Alert should be displayed when I left repeat your password form empty
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "Pa$$word123@" into "Password" field on the "Register" Page
    And I left empty "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "Passwords does not match!" should be displayed under "Repeat your password" field on the "Register" Page

  Scenario: Alert should be displayed when I enter not valid password
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I enter "test@test.pl" into "Email" field on the "Register" Page
    And I enter "Test User" into "Full name" field on the "Register" Page
    And I enter "password" into "Password" field on the "Register" Page
    And I enter "password" into "Repeat your password" field on the "Register" Page
    And I click on "Register" button on the "Register" Page
    Then Alert "Password must contain 8 characters, one uppercase, one lowercase, one number and one special case character!" should be displayed under "Password" field on the "Register" Page

  Scenario: I should be redirected to Login Page
    Given I am on the site homepage
    When I click on "Register" button on the "Home" Page
    And I click on "Login instead" button on "Register" Page
    Then I should be reddirected to "Login" Page

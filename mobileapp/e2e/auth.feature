Feature: Auth and key upload

  Scenario: I can log in properly with correct credentials when keys are not uploaded
    Given User "admin@admin.pl" exists with password "admin" and role "ADMIN"
    And User "admin@admin.pl" has key uploaded locally
    And I am at the "login" page
    And I Enter "admin@admin.pl" into "email" field
    And I Enter "admin" into "password" field
    And I Enter "http://10.0.0.2:8080 into "url" field
    When I click "Login" button
    Then I am redirected to the "Home" screen

  Scenario: I can log in properly with correct credentials when keys are not uploaded
    Given User "admin2@admin.pl" exists with password "admin" and role "ADMIN"
    And I am at the "login" page
    And I Enter "admin2@admin.pl" into "email" field
    And I Enter "admin" into "password" field
    And I Enter "http://10.0.0.2:8080 into "url" field
    When I click "Login" button
    Then I am redirected to the "Key upload" screen
    And I see "Uploading keys" text
    And I am redirected to the "Home" screen

  Scenario: I can't log in with invalid password
    Given User "admin@admin.pl" exists with password "admin" and role "ADMIN"
    And User "admin@admin.pl" has key uploaded locally
    And I am at the "login" page
    And I Enter "admin2@admin.pl" into "email" field
    And I Enter "admin123" into "password" field
    And I Enter "http://10.0.0.2:8080 into "url" field
    When I click "Login" button
    Then I am at the "login" page
    And I see "Invalid credentials" text

  Scenario: I can't log in with invalid email
    Given User "admin@admin.pl" exists with password "admin" and role "ADMIN"
    And User "admin@admin.pl" has key uploaded locally
    And I am at the "login" page
    And I Enter "admin@aasddmin.pl" into "email" field
    And I Enter "admin" into "password" field
    And I Enter "http://10.0.0.2:8080 into "url" field
    When I click "Login" button
    Then I am at the "login" page
    And I see "Invalid credentials" text

  Scenario: I can't log in when url is invalid
    Given User "admin@admin.pl" exists with password "admin" and role "ADMIN"
    And User "admin@admin.pl" has key uploaded locally
    And I am at the "login" page
    And I Enter "admin@aasddmin.pl" into "email" field
    And I Enter "admin" into "password" field
    And I Enter "htt.0.0.2:8080 into "url" field
    When I click "Login" button
    Then I am at the "login" page
    And I see "Invalid url" text

  Scenario: I can't login when key was upload on another device
    Given User "admin@admin.pl" exists with password "admin" and role "ADMIN"
    And User "admin@admin.pl" has key uploaded on another device
    And I am at the "login" page
    And I Enter "admin@admin.pl" into "email" field
    And I Enter "admin" into "password" field
    And I Enter "http://10.0.0.2:8080 into "url" field
    When I click "Login" button
    Then I am at the "login" page
    And I see "Key already uploaded" text

  Scenario: I can't login when I am a standard user
    Given User "admin@admin.pl" exists with password "admin" and role "USER"
    And User "admin@admin.pl" has key uploaded on another device
    And I am at the "login" page
    And I Enter "admin@admin.pl" into "email" field
    And I Enter "admin" into "password" field
    And I Enter "http://10.0.0.2:8080 into "url" field
    When I click "Login" button
    Then I am at the "login" page
    And I see "This app is for admins only" text

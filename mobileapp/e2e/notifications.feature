Feature: Notifications

  Scenario: I should see a list of notifications:
    Given Logged user "admin3@admin.pl"
    And I am at the "notifications" page
    Then I see "10" notifications

  Scenario: I should load more notifications when I scroll to the bottom
    Given Logged user "admin3@admin.pl"
    And I am at the "notifications" page
    When I scroll to the bottom of the screen
    Then I see "15" notifications

  Scenario: I should see error message when no notifications
    Given Logged user "admin@admin.pl"
    And I am at the "notifications" page
    Then I see "0" notifications
    And I see "Nothing there yet" text

  Scenario: I should be able to refresh screen
    Given Logged user "admin3@admin.pl"
    And I am at the "notifications" page
    And I see "10" notifications
    When I pull the screen up
    Then I should see a loading spinner
    And I see "10" notifications

  Scenario: I can browse denied notification details
    Given Logged user "admin3@admin.pl"
    And I am at the "notifications" page
    When I click on first notification with "DENIED" status
    Then I am redirected to "Notification details" screen
    And I see "Transaction confirmation" text
    And I see "10000 sat" text
    But I should not see "Confirm" button
    But I should not see "Deny" button

  Scenario: I can browse confirmed notification details
    Given Logged user "admin3@admin.pl"
    And I am at the "notifications" page
    When I click on first notification with "CONFIRMED" status
    Then I am redirected to "Notification details" screen
    And I see "Transaction confirmation" text
    And I see "10000 sat" text
    But I should not see "Confirm" button
    But I should not see "Deny" button

  Scenario: I can confirm a pending transaction
    Given Logged user "admin3@admin.pl"
    And I am at the "notifications" page
    And I click on first notification with "PENDING" status
    When I click "Confirm button"
    Then I see "Processing" text
    And I am redirected to "Notification result" screen
    And I see "Transaction confirmed successfully!"
    And I click "OK" button
    And I am redirected to "notifications" page

  Scenario: I can deny a pending transaction
    Given Logged user "admin3@admin.pl"
    And I am at the "notifications" page
    And I click on first notification with "PENDING" status
    When I click "Confirm button"
    Then I see "Processing" text
    And I am redirected to "Notification result" screen
    And I see "Transaction has been denied!"
    And I click "OK" button
    And I am redirected to "notifications" page

  Scenario: I receive push notification when new transaction is registered
    Given Logged user "admin3@admin.pl"
    And I am at the "notifications" page
    And New notification is send from the backend
    Then Push notification appears on the top bar
    And I click on the notification
    And I am redirected to "Notification details" screen

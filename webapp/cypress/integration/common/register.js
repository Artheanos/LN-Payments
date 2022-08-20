import { Then } from 'cypress-cucumber-preprocessor/steps'

Then(
  'Alert {string} is displayed under {string} field',
  (alert_under_field) => {
    cy.contains(alert_under_field).should('exist')
  }
)

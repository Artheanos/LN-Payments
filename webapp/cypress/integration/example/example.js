import { When, Then } from 'cypress-cucumber-preprocessor/steps'

When('I enter main page', () => {
  cy.visit('/')
})

Then('It contains LNPayments button', () => {
  cy.contains('LN Payments')
})

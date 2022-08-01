// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })

import { And, Given, Then, When } from 'cypress-cucumber-preprocessor/steps'

Cypress.Commands.add('redirectToPage', () => {
  Then('I am redirected to {string} Page', (page) => {
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq(page)
    })
  })
})

Cypress.Commands.add('typeData', () => {
  Given('I enter {string} into {string} field', (data, data_field) => {
    cy.get('input[name=' + data_field + ']').type(data)
  })
})

Cypress.Commands.add('clickButton', () => {
  When('I click on {string} button', (btn) => {
    cy.get('button').contains(btn).click()
  })
})

Cypress.Commands.add('displayAlert', () => {
  And('Alert {string} is displayed', (alert) => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal(alert)
    })
  })
})

Cypress.Commands.add('checkIfAlertExist', () => {
  Then(
    'Alert {string} is displayed under {string} field',
    (alert_under_field) => {
      cy.contains(alert_under_field).should('exist')
    }
  )
})

import { Then } from 'cypress-cucumber-preprocessor/steps'

Then('Alert {string} is displayed', (alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(alert)
  })
})

import { Given, Then, When } from 'cypress-cucumber-preprocessor/steps'

Given('I am on the site homepage', () => {
  cy.visit('http://localhost:3000/')
})

When('I click on {string} button', (btn) => {
  cy.get('button').contains(btn).click()
})

Then('I am redirected to {string} Page', (page) => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq(page)
  })
})

Given('I enter {string} into {string} field', (data, data_field) => {
  cy.get('input[name=' + data_field + ']').type(data)
})

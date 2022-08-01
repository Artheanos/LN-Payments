import { Then, Given, And } from 'cypress-cucumber-preprocessor/steps'
// eslint-disable-next-line no-restricted-imports
import '../../support/index'

beforeEach(() => {
  Given('I am on the site homepage', () => {
    cy.visit('http://localhost:3000/')
  })
  cy.clickButton()
  cy.redirectToPage()
})

it('I am registered when I fill form correctly', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  Then('Alert {string} is displayed', (successful_msg) => {
    cy.on('window:confirm', (text) => {
      expect(text).to.eq(successful_msg)
    })
  })
  And(
    'I am redirected to {string} Page after click {string} button from alert',
    (login_page, confirm_button) => {
      cy.contains(confirm_button).click()
      cy.location().should((loc) => {
        expect(loc.pathname).to.eq(login_page)
      })
    }
  )
})

it('Alert is displayed when user with that email already exist', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alert is displayed when I enter not valid email', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alert is displayed when I left email form empty', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alert is displayed when I left full name form empty', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alert is displayed when I enter not valid full name', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alerts is displayed when I left password form empty', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alerts is displayed when I left password and repeat your password forms empty', () => {
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alert is displayed when I left repeat your password form empty', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alert is displayed when I enter not valid password', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('Alert is displayed when I enter not valid password', () => {
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.checkIfAlertExist()
})

it('I am redirected to Login Page', () => {
  cy.clickButton()
  cy.redirectToPage()
})

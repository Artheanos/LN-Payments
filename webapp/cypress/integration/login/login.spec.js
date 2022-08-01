import { Given } from 'cypress-cucumber-preprocessor/steps'
// eslint-disable-next-line no-restricted-imports
import '../../support/index'

beforeEach(() => {
  Given('I am on the site homepage', () => {
    cy.visit('http://localhost:3000/')
  })
  cy.clickButton()
  cy.redirectToPage()
})

it('I am logged in when I enter correct email and password credentials', () => {
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.redirectToPage()
  cy.displayAlert()
})

it('Alert is displayed when I did not enter email and password credentials', () => {
  cy.clickButton()
  cy.displayAlert()
})

it('Alert is displayed when I did not enter email credential', () => {
  cy.typeData()
  cy.clickButton()
  cy.displayAlert()
})

it('Alert is displayed when I did not enter password credential', () => {
  cy.typeData()
  cy.clickButton()
  cy.displayAlert()
})

it('Alert is displayed when I enter incorrect email and password credentials', () => {
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.displayAlert()
})

it('Alert is displayed when I enter incorrect email and correct password credentials', () => {
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.displayAlert()
})

it('Alert is displayed when I enter correct email and incorrect password credentials', () => {
  cy.typeData()
  cy.typeData()
  cy.clickButton()
  cy.displayAlert()
})

it('I am redirected to Register Page', () => {
  cy.clickButton()
  cy.redirectToPage()
})

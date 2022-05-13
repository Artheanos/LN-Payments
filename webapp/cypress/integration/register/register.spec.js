import { Then, And } from 'cypress-cucumber-preprocessor/steps'

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

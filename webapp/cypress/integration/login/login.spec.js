import { Then, When, Given, And } from 'cypress-cucumber-preprocessor/steps'

beforeEach(() => {
  Given('I am on the site homepage', () => {
    cy.visit('http://localhost:3000/')
  })
  When('I click on {string} button', (login_button) => {
    cy.get('button').contains(login_button).click()
  })
  Then('I am redirected to {string} Page', (login_page) => {
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq(login_page)
    })
  })
})

Given('I enter {string} into {string} field', (correct_login, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_login)
})
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
When('I click on {string} button', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('I am redirected to {string} Page of {string} User', (history_page) => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq(history_page)
  })
})
And('Alert {string} is displayed for {string} User', (login_success_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(login_success_alert)
  })
})

When('I click on {string} button', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
When('I click on {string} button', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given('I enter {string} into {string} field', (correct_login, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_login)
})
When('I click on {string} button', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given(
  'I enter {string} into {string} field',
  (incorrect_login, email_field) => {
    cy.get('input[name=' + email_field + ']').type(incorrect_login)
  }
)
And(
  'I enter {string} into {string} field',
  (incorrect_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(incorrect_password)
  }
)
When('I click on {string} button', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given(
  'I enter {string} into {string} field',
  (incorrect_login, email_field) => {
    cy.get('input[name=' + email_field + ']').type(incorrect_login)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
When('I click on {string} button', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given('I enter {string} into {string} field', (correct_login, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_login)
})
And(
  'I enter {string} into {string} field',
  (incorrect_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(incorrect_password)
  }
)
When('I click on {string} button', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

When('I click on {string} button', (register_instead_button) => {
  cy.get('button').contains(register_instead_button).click()
})
Then('I am redirected to {string} Page', (register_page) => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq(register_page)
  })
})

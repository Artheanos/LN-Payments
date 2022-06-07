import { Then, When, Given, And } from 'cypress-cucumber-preprocessor/steps'

beforeEach(() => {
  Given('I am on the site homepage', () => {
    cy.visit('http://localhost:3000/')
  })
  When('I click on {string} button on the "Home" Page', (register_button) => {
    cy.get('button').contains(register_button).click()
  })
  Then('I am redirected to "Register" Page', () => {
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq('/register')
    })
  })
})

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (correct_email) => {
    cy.get('input[name=email]').type(correct_email)
  }
)
And(
  'I enter {string} into "Full name" field on the "Register" Page',
  (correct_username) => {
    cy.get('input[name=fullName').type(correct_username)
  }
)
And(
  'I enter {string} into "Password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
And(
  'I enter {string} into "Repeat your password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=passwordConfirmation').type(correct_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then('Alert {string} is displayed on "Register" Page', (successful_msg) => {
  cy.on('window:confirm', (text) => {
    expect(text).to.eq(successful_msg)
  })
})
And(
  'I am redirected to "Login" Page after click {string} button from alert',
  (confirm_button) => {
    cy.contains(confirm_button).click()
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq('/login')
    })
  }
)

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (correct_email) => {
    cy.get('input[name=email]').type(correct_email)
  }
)
And(
  'I enter {string} into "Full name" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=fullName').type(correct_password)
  }
)
And(
  'I enter {string} into "Password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
And(
  'I enter {string} into "Repeat your password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=passwordConfirmation').type(correct_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Email" field on the "Register" Page',
  (alert_email) => {
    cy.contains(alert_email).should('exist')
  }
)

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (incorrect_email) => {
    cy.get('input[name=email]').type(incorrect_email)
  }
)
And(
  'I enter {string} into "Full name" field on the "Register" Page',
  (correct_username) => {
    cy.get('input[name=fullName').type(correct_username)
  }
)
And(
  'I enter {string} into "Password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
And(
  'I enter {string} into "Repeat your password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=passwordConfirmation').type(correct_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Email" field on the "Register" Page',
  (alert_email_not_valid) => {
    cy.contains(alert_email_not_valid).should('exist')
  }
)

Given(
  'I enter {string} into "Full name" field on the "Register" Page',
  (correct_username) => {
    cy.get('input[name=fullName').type(correct_username)
  }
)
And(
  'I enter {string} into "Password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
And(
  'I enter {string} into "Repeat your password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=passwordConfirmation').type(correct_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Email" field on the "Register" Page',
  (alert_email_required) => {
    cy.contains(alert_email_required).should('exist')
  }
)

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (correct_email) => {
    cy.get('input[name=email]').type(correct_email)
  }
)
And(
  'I enter {string} into "Password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
And(
  'I enter {string} into "Repeat your password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=passwordConfirmation').type(correct_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Full name" field on the "Register" Page',
  (alert_name_required) => {
    cy.contains(alert_name_required).should('exist')
  }
)

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (correct_email) => {
    cy.get('input[name=email]').type(correct_email)
  }
)
And(
  'I enter {string} into "Full name" field on the "Register" Page',
  (incorrect_username) => {
    cy.get('input[name=fullName').type(incorrect_username)
  }
)
And(
  'I enter {string} into "Password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
And(
  'I enter {string} into "Repeat your password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=passwordConfirmation').type(correct_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Full name" field on the "Register" Page',
  (alert_name_not_valid) => {
    cy.contains(alert_name_not_valid).should('exist')
  }
)

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (correct_email) => {
    cy.get('input[name=email]').type(correct_email)
  }
)
And(
  'I enter {string} into "Full name" field on the "Register" Page',
  (correct_username) => {
    cy.get('input[name=fullName').type(correct_username)
  }
)
And(
  'I enter {string} into "Repeat your password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=passwordConfirmation').type(correct_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Password" field on the "Register" Page',
  (alert_password_required) => {
    cy.contains(alert_password_required).should('exist')
  }
)
And(
  'Alert {string} is displayed under "Repeat your password" field on the "Register" Page',
  (alert_password_not_match) => {
    cy.contains(alert_password_not_match).should('exist')
  }
)

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (correct_email) => {
    cy.get('input[name=email]').type(correct_email)
  }
)
And(
  'I enter {string} into "Full name" field on the "Register" Page',
  (correct_username) => {
    cy.get('input[name=fullName').type(correct_username)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Password" field on the "Register" Page',
  (alert_password_required) => {
    cy.contains(alert_password_required).should('exist')
  }
)
And(
  'Alert {string} is displayed under "Repeat your password" field on the "Register" Page',
  (alert_password_not_match) => {
    cy.contains(alert_password_not_match).should('exist')
  }
)

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (correct_email) => {
    cy.get('input[name=email]').type(correct_email)
  }
)
And(
  'I enter {string} into "Full name" field on the "Register" Page',
  (correct_username) => {
    cy.get('input[name=fullName').type(correct_username)
  }
)
And(
  'I enter {string} into "Password" field on the "Register" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Repeat your password" field on the "Register" Page',
  (alert_password_not_match) => {
    cy.contains(alert_password_not_match).should('exist')
  }
)

Given(
  'I enter {string} into "Email" field on the "Register" Page',
  (correct_email) => {
    cy.get('input[name=email]').type(correct_email)
  }
)
And(
  'I enter {string} into "Full name" field on the "Register" Page',
  (correct_username) => {
    cy.get('input[name=fullName').type(correct_username)
  }
)
And(
  'I enter {string} into "Password" field on the "Register" Page',
  (incorrect_password) => {
    cy.get('input[name=password').type(incorrect_password)
  }
)
And(
  'I enter {string} into "Repeat your password" field on the "Register" Page',
  (incorrect_password) => {
    cy.get('input[name=passwordConfirmation').type(incorrect_password)
  }
)
When('I click on {string} button on the "Register" Page', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under "Password" field on the "Register" Pages',
  (alert_incorrect_password) => {
    cy.contains(alert_incorrect_password).should('exist')
  }
)

When(
  'I click on {string} button on "Register" Page',
  (login_instead_button) => {
    cy.get('button').contains(login_instead_button).click()
  }
)
Then('I am redirected to "Login" Page', () => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq('/login')
  })
})

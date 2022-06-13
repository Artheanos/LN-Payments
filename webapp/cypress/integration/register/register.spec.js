import { Then, When, Given, And } from 'cypress-cucumber-preprocessor/steps'

beforeEach(() => {
  Given('I am on the site homepage', () => {
    cy.visit('http://localhost:3000/')
  })
  When('I click on {string} button', (register_button) => {
    cy.get('button').contains(register_button).click()
  })
  Then('I am redirected to {string} Page', (register_page) => {
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq(register_page)
    })
  })
})

Given('I enter {string} into {string} field', (correct_email, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_email)
})
And(
  'I enter {string} into {string} field',
  (correct_username, fullName_field) => {
    cy.get('input[name=' + fullName_field + '').type(correct_username)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
And(
  'I enter {string} into {string} field on the "Register" Page',
  (correct_password, passwordConfirm_field) => {
    cy.get('input[name=' + passwordConfirm_field + '').type(correct_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
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

Given('I enter {string} into {string} field', (correct_email, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_email)
})
And('I enter {string} into {string} field', (correct_name, fullName_field) => {
  cy.get('input[name=' + fullName_field + '').type(correct_name)
})
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, passwordConfirm_field) => {
    cy.get('input[name=' + passwordConfirm_field + '').type(correct_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field on the "Register" Page',
  (alert_email) => {
    cy.contains(alert_email).should('exist')
  }
)

Given(
  'I enter {string} into {string} field',
  (incorrect_email, email_field) => {
    cy.get('input[name=' + email_field + ']').type(incorrect_email)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_username, fullName_field) => {
    cy.get('input[name=' + fullName_field + '').type(correct_username)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, passwordConfirm_field) => {
    cy.get('input[name=' + passwordConfirm_field + '').type(correct_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field',
  (alert_email_not_valid) => {
    cy.contains(alert_email_not_valid).should('exist')
  }
)

Given(
  'I enter {string} into {string} field',
  (correct_username, fullName_field) => {
    cy.get('input[name=' + fullName_field + '').type(correct_username)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, passwordConfirm_field) => {
    cy.get('input[name=' + passwordConfirm_field + '').type(correct_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field',
  (alert_email_required) => {
    cy.contains(alert_email_required).should('exist')
  }
)

Given('I enter {string} into {string} field', (correct_email, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_email)
})
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, passwordConfirm_field) => {
    cy.get('input[name=' + passwordConfirm_field + '').type(correct_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field',
  (alert_name_required) => {
    cy.contains(alert_name_required).should('exist')
  }
)

Given('I enter {string} into {string} field', (correct_email, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_email)
})
And(
  'I enter {string} into {string} field',
  (incorrect_username, fullName_field) => {
    cy.get('input[name=' + fullName_field + '').type(incorrect_username)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, passwordConfirm_field) => {
    cy.get('input[name=' + passwordConfirm_field + '').type(correct_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field',
  (alert_name_not_valid) => {
    cy.contains(alert_name_not_valid).should('exist')
  }
)

Given('I enter {string} into {string} field', (correct_email, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_email)
})
And(
  'I enter {string} into {string} field',
  (correct_username, fullName_field) => {
    cy.get('input[name=' + fullName_field + '').type(correct_username)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, passwordConfirm_field) => {
    cy.get('input[name=' + passwordConfirm_field + '').type(correct_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field',
  (alert_password_required) => {
    cy.contains(alert_password_required).should('exist')
  }
)
And(
  'Alert {string} is displayed under {string} field',
  (alert_password_not_match) => {
    cy.contains(alert_password_not_match).should('exist')
  }
)

Given('I enter {string} into {string} field', (correct_email, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_email)
})
And(
  'I enter {string} into {string} field',
  (correct_username, fullName_field) => {
    cy.get('input[name=' + fullName_field + '').type(correct_username)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field',
  (alert_password_required) => {
    cy.contains(alert_password_required).should('exist')
  }
)
And(
  'Alert {string} is displayed under {string} field',
  (alert_password_not_match) => {
    cy.contains(alert_password_not_match).should('exist')
  }
)

Given('I enter {string} into {string} field', (correct_email, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_email)
})
And(
  'I enter {string} into {string} field',
  (correct_username, fullName_field) => {
    cy.get('input[name=' + fullName_field + '').type(correct_username)
  }
)
And(
  'I enter {string} into {string} field',
  (correct_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(correct_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field',
  (alert_password_not_match) => {
    cy.contains(alert_password_not_match).should('exist')
  }
)

Given('I enter {string} into {string} field', (correct_email, email_field) => {
  cy.get('input[name=' + email_field + ']').type(correct_email)
})
And(
  'I enter {string} into {string} field',
  (correct_username, fullName_field) => {
    cy.get('input[name=' + fullName_field + '').type(correct_username)
  }
)
And(
  'I enter {string} into {string} field',
  (incorrect_password, password_field) => {
    cy.get('input[name=' + password_field + '').type(incorrect_password)
  }
)
And(
  'I enter {string} into {string} field',
  (incorrect_password, passwordConfirm_field) => {
    cy.get('input[name=' + passwordConfirm_field + '').type(incorrect_password)
  }
)
When('I click on {string} button', (register_button) => {
  cy.get('button').contains(register_button).click()
})
Then(
  'Alert {string} is displayed under {string} field',
  (alert_incorrect_password) => {
    cy.contains(alert_incorrect_password).should('exist')
  }
)

When('I click on {string} button', (login_instead_button) => {
  cy.get('button').contains(login_instead_button).click()
})
Then('I am redirected to {string} Page', (login_page) => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq(login_page)
  })
})

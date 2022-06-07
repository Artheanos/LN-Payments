import { Then, When, Given, And } from 'cypress-cucumber-preprocessor/steps'

beforeEach(() => {
  Given('I am on the site homepage', () => {
    cy.visit('http://localhost:3000/')
  })
  When('I click on {string} button on the "Home" Page', (login_button) => {
    cy.get('button').contains(login_button).click()
  })
  Then('I am redirected to "Login" Page', () => {
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq('/login')
    })
  })
})

Given(
  'I enter {string} into "Email" field on the "Login" Page',
  (correct_login) => {
    cy.get('input[name=email]').type(correct_login)
  }
)
And(
  'I enter {string} into "Password" field on the "Login" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
When('I click on {string} button on the "Login" Page', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('I am redirected to "History" Page of "admin@admin.pl" User', () => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq('/panel/history')
  })
})
And(
  'Alert {string} is displayed on "History" Page of "admin@admin.pl" User',
  (login_success_alert) => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal(login_success_alert)
    })
  }
)

When('I click on {string} button on the "Login" Page', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed on "Login" Page', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given(
  'I enter {string} into "Password" field on the "Login" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
When('I click on {string} button on the "Login" Page', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed on "Login" Page', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given(
  'I enter {string} into "Email" field on the "Login" Page',
  (correct_login) => {
    cy.get('input[name=email]').type(correct_login)
  }
)
When('I click on {string} button on the "Login" Page', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed on "Login" Page', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given(
  'I enter {string} into "Email" field on the "Login" Page',
  (incorrect_login) => {
    cy.get('input[name=email]').type(incorrect_login)
  }
)
And(
  'I enter {string} into "Password" field on the "Login" Page',
  (incorrect_password) => {
    cy.get('input[name=password').type(incorrect_password)
  }
)
When('I click on {string} button on the "Login" Page', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed on "Login" Page', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given(
  'I enter {string} into "Email" field on the "Login" Page',
  (incorrect_login) => {
    cy.get('input[name=email]').type(incorrect_login)
  }
)
And(
  'I enter {string} into "Password" field on the "Login" Page',
  (correct_password) => {
    cy.get('input[name=password').type(correct_password)
  }
)
When('I click on {string} button on the "Login" Page', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed on "Login" Page', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

Given(
  'I enter {string} into "Email" field on the "Login" Page',
  (correct_login) => {
    cy.get('input[name=email]').type(correct_login)
  }
)
And(
  'I enter {string} into "Password" field on the "Login" Page',
  (incorrect_password) => {
    cy.get('input[name=password').type(incorrect_password)
  }
)
When('I click on {string} button on the "Login" Page', (login_button) => {
  cy.get('button').contains(login_button).click()
})
Then('Alert {string} is displayed on "Login" Page', (email_password_alert) => {
  cy.on('window:alert', (str) => {
    expect(str).to.equal(email_password_alert)
  })
})

When(
  'I click on {string} button on "Login" Page',
  (register_instead_button) => {
    cy.get('button').contains(register_instead_button).click()
  }
)
Then('I am redirected to "Register" Page', () => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq('/register')
  })
})

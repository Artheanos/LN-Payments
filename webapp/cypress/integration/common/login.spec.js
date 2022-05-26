import { Then, When, Given, And } from 'cypress-cucumber-preprocessor/steps'

beforeEach(() => {
  Given('I am on the site homepage', () => {
    cy.visit('http://localhost:3000/')
  })
  When('I click on "Login" button on the "Home" Page', () => {
    cy.get('button').contains('Login').click()
  })
  Then('I am redirected to "Login" Page', () => {
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq('/login')
    })
  })
})

Given('I enter "admin@admin.pl" into "Email" field on the "Login" Page', () => {
  cy.get('input[name=email]').type('admin@admin.pl')
})
And('I enter "admin" into "Password" field on the "Login" Page', () => {
  cy.get('input[name=password').type('admin')
})
When('I click on "Login" button on the "Login" Page', () => {
  cy.get('button').contains('Login').click()
})
Then('I am redirected to "History" Page of "admin@admin.pl" User', () => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq('/panel/history')
  })
})
And(
  'Alert "Login successful" is displayed on "History" Page of "admin@admin.pl" User',
  () => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal('Login successful')
    })
  }
)

Given('I left empty "Email" field on the "Login" Page', () => {})
And('I left empty "Password" field on the "Login" Page', () => {})
When('I click on "Login" button on the "Login" Page', () => {
  cy.get('button').contains('Login').click()
})
Then(
  'Alert "Email or password is invalid" is displayed on "Login" Page',
  () => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal('Email or password is invalid')
    })
  }
)

Given('I left empty "Email" field on the "Login" Page', () => {})
And('I enter "admin" into "Password" field on the "Login" Page', () => {
  cy.get('input[name=password').type('admin')
})
When('I click on "Login" button on the "Login" Page', () => {
  cy.get('button').contains('Login').click()
})
Then(
  'Alert "Email or password is invalid" is displayed on "Login" Page',
  () => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal('Email or password is invalid')
    })
  }
)

Given('I enter "admin@admin.pl" into "Email" field on the "Login" Page', () => {
  cy.get('input[name=email]').type('admin@admin.pl')
})
And('I left empty "Password" field on the "Login" Page', () => {})
When('I click on "Login" button on the "Login" Page', () => {
  cy.get('button').contains('Login').click()
})
Then(
  'Alert "Email or password is invalid" is displayed on "Login" Page',
  () => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal('Email or password is invalid')
    })
  }
)

Given('I enter "test@test.pl" into "Email" field on the "Login" Page', () => {
  cy.get('input[name=email]').type('test@test.pl')
})
And('I enter "test" into "Password" field on the "Login" Page', () => {
  cy.get('input[name=password').type('test')
})
When('I click on "Login" button on the "Login" Page', () => {
  cy.get('button').contains('Login').click()
})
Then(
  'Alert "Email or password is invalid" is displayed on "Login" Page',
  () => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal('Email or password is invalid')
    })
  }
)

Given(
  'I enter "adminzzz@admin.pl" into "Email" field on the "Login" Page',
  () => {
    cy.get('input[name=email]').type('adminzzz@admin.pl')
  }
)
And('I enter "admin" into "Password" field on the "Login" Page', () => {
  cy.get('input[name=password').type('admin')
})
When('I click on "Login" button on the "Login" Page', () => {
  cy.get('button').contains('Login').click()
})
Then(
  'Alert "Email or password is invalid" is displayed on "Login" Page',
  () => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal('Email or password is invalid')
    })
  }
)

Given('I enter "admin@admin.pl" into "Email" field on the "Login" Page', () => {
  cy.get('input[name=email]').type('admin@admin.pl')
})
And('I enter "adminzzz" into "Password" field on the "Login" Page', () => {
  cy.get('input[name=password').type('adminzzz')
})
When('I click on "Login" button on the "Login" Page', () => {
  cy.get('button').contains('Login').click()
})
Then(
  'Alert "Email or password is invalid" is displayed on "Login" Page',
  () => {
    cy.on('window:alert', (str) => {
      expect(str).to.equal('Email or password is invalid')
    })
  }
)

When('I click on "Register instead" button on "Login" Page', () => {
  cy.get('button').contains('Register instead').click()
})
Then('I am redirected to "Register" Page', () => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq('/register')
  })
})

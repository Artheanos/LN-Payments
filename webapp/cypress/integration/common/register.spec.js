import { Then, When, Given, And } from 'cypress-cucumber-preprocessor/steps'

beforeEach(() => {
  Given('I am on the site homepage', () => {
    cy.visit('http://localhost:3000/')
  })
  When('I click on "Register" button on the "Home" Page', () => {
    cy.get('button').contains('Register').click()
  })
  Then('I am redirected to "Register" Page', () => {
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq('/register')
    })
  })
})

Given(
  'I enter "test@test.pl" into "Email" field on the "Register" Page',
  () => {
    cy.get('input[name=email]').type('test@test.pl')
  }
)
And('I enter "Test User" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('Test User')
})
And(
  'I enter "Pa$$word123@" into "Password" field on the "Register" Page',
  () => {
    cy.get('input[name=password').type('Pa$$word123@')
  }
)
And(
  'I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page',
  () => {
    cy.get('input[name=passwordConfirmation').type('Pa$$word123@')
  }
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "You have been successfully registered. You can now log in!" is displayed on "Register" Page',
  () => {
    cy.on('window:confirm', (text) => {
      expect(text).to.eq(
        'You have been successfully registered. You can now log in!'
      )
    })
  }
)
And(
  'I am redirected to "Login" Page after click "Ok" button from alert',
  () => {
    cy.contains('Ok').click()
    cy.location().should((loc) => {
      expect(loc.pathname).to.eq('/login')
    })
  }
)

Given(
  'I enter "test@test.pl" into "Email" field on the "Register" Page',
  () => {
    cy.get('input[name=email]').type('test@test.pl')
  }
)
And('I enter "Test User" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('Test User')
})
And(
  'I enter "Pa$$word123@" into "Password" field on the "Register" Page',
  () => {
    cy.get('input[name=password').type('Pa$$word123@')
  }
)
And(
  'I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page',
  () => {
    cy.get('input[name=passwordConfirmation').type('Pa$$word123@')
  }
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Given email is already in use" is displayed under "Email" field on the "Register" Page',
  () => {
    cy.contains('Given email is already in use').should('exist')
  }
)

Given('I enter "test123.pl" into "Email" field on the "Register" Page', () => {
  cy.get('input[name=email]').type('test123.pl')
})
And('I enter "Test User" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('Test User')
})
And(
  'I enter "Pa$$word123@" into "Password" field on the "Register" Page',
  () => {
    cy.get('input[name=password').type('Pa$$word123@')
  }
)
And(
  'I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page',
  () => {
    cy.get('input[name=passwordConfirmation').type('Pa$$word123@')
  }
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Email not valid!" is displayed under "Email" field on the "Register" Page',
  () => {
    cy.contains('Email not valid!').should('exist')
  }
)

Given('I left empty "Email" field on the "Register" Page', () => {})
And('I enter "Test User" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('Test User')
})
And(
  'I enter "Pa$$word123@" into "Password" field on the "Register" Page',
  () => {
    cy.get('input[name=password').type('Pa$$word123@')
  }
)
And(
  'I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page',
  () => {
    cy.get('input[name=passwordConfirmation').type('Pa$$word123@')
  }
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Email is required!" is displayed under "Email" field on the "Register" Page',
  () => {
    cy.contains('Email is required!').should('exist')
  }
)

Given(
  'I enter "test@test.pl" into "Email" field on the "Register" Page',
  () => {
    cy.get('input[name=email]').type('test@test.pl')
  }
)
And('I left empty "Full name" field on the "Register" Page', () => {})
And(
  'I enter "Pa$$word123@" into "Password" field on the "Register" Page',
  () => {
    cy.get('input[name=password').type('Pa$$word123@')
  }
)
And(
  'I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page',
  () => {
    cy.get('input[name=passwordConfirmation').type('Pa$$word123@')
  }
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Full name is required!" is displayed under "Full name" field on the "Register" Page',
  () => {
    cy.contains('Full name is required!').should('exist')
  }
)

Given(
  'I enter "test@test.pl" into "Email" field on the "Register" Page',
  () => {
    cy.get('input[name=email]').type('test@test.pl')
  }
)
And('I enter "NaMe@123&" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('NaMe@123&')
})
And(
  'I enter "Pa$$word123@" into "Password" field on the "Register" Page',
  () => {
    cy.get('input[name=password').type('Pa$$word123@')
  }
)
And(
  'I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page',
  () => {
    cy.get('input[name=passwordConfirmation').type('Pa$$word123@')
  }
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Full name is required!" is displayed under "Full name" field on the "Register" Page',
  () => {
    cy.contains('Full name not valid!').should('exist')
  }
)

Given(
  'I enter "test@test.pl" into "Email" field on the "Register" Page',
  () => {
    cy.get('input[name=email]').type('test@test.pl')
  }
)
And('I enter "Test User" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('Test User')
})
And('I left empty "Password" field on the "Register" Page', () => {})
And(
  'I enter "Pa$$word123@" into "Repeat your password" field on the "Register" Page',
  () => {
    cy.get('input[name=passwordConfirmation').type('Pa$$word123@')
  }
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Password is required!" is displayed under "Password" field on the "Register" Page',
  () => {
    cy.contains('Password is required!').should('exist')
  }
)
And(
  'Alert "Passwords does not match!" is displayed under "Repeat your password" field on the "Register" Page',
  () => {
    cy.contains('Passwords does not match!').should('exist')
  }
)

Given(
  'I enter "test@test.pl" into "Email" field on the "Register" Page',
  () => {
    cy.get('input[name=email]').type('test@test.pl')
  }
)
And('I enter "Test User" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('Test User')
})
And('I left empty "Password" field on the "Register" Page', () => {})
And(
  'I left empty "Repeat your password" field on the "Register" Page',
  () => {}
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Password is required!" is displayed under "Password" field on the "Register" Page',
  () => {
    cy.contains('Password is required!').should('exist')
  }
)
And(
  'Alert "Passwords does not match!" is displayed under "Repeat your password" field on the "Register" Page',
  () => {
    cy.contains('Passwords does not match!').should('exist')
  }
)

Given(
  'I enter "test@test.pl" into "Email" field on the "Register" Page',
  () => {
    cy.get('input[name=email]').type('test@test.pl')
  }
)
And('I enter "Test User" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('Test User')
})
And(
  'I enter "Pa$$word123@" into "Password" field on the "Register" Page',
  () => {
    cy.get('input[name=password').type('Pa$$word123@')
  }
)
And(
  'I left empty "Repeat your password" field on the "Register" Page',
  () => {}
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Passwords does not match!" is displayed under "Repeat your password" field on the "Register" Page',
  () => {
    cy.contains('Passwords does not match!').should('exist')
  }
)

Given(
  'I enter "test@test.pl" into "Email" field on the "Register" Page',
  () => {
    cy.get('input[name=email]').type('test@test.pl')
  }
)
And('I enter "Test User" into "Full name" field on the "Register" Page', () => {
  cy.get('input[name=fullName').type('Test User')
})
And('I enter "password" into "Password" field on the "Register" Page', () => {
  cy.get('input[name=password').type('password')
})
And(
  'I enter "password" into "Repeat your password" field on the "Register" Page',
  () => {
    cy.get('input[name=passwordConfirmation').type('password')
  }
)
When('I click on "Register" button on the "Register" Page', () => {
  cy.get('button').contains('Register').click()
})
Then(
  'Alert "Password must contain 8 characters, one uppercase, one lowercase, one number and one special case character!"is displayed under "Password" field on the "Register" Page',
  () => {
    cy.contains('Password must contain 8 characters').should('exist')
  }
)

When('I click on "Login instead" button on "Register" Page', () => {
  cy.get('button').contains('Login instead').click()
})
Then('I am redirected to "Login" Page', () => {
  cy.location().should((loc) => {
    expect(loc.pathname).to.eq('/login')
  })
})

// eslint-disable-next-line @typescript-eslint/no-var-requires
const cucumber = require('cypress-cucumber-preprocessor').default

// eslint-disable-next-line no-unused-vars
module.exports = (on) => {
  on('file:preprocessor', cucumber())
}

module.exports = {
  preset: 'jest-expo',
  setupFilesAfterEnv: ['@testing-library/jest-native/extend-expect'],
  setupFiles: ['./tests/jestSetupFile.js'],
  modulePaths: ['<rootDir>/'],
}

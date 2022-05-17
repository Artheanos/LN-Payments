module.exports = {
  testEnvironment: 'jsdom',
  testPathIgnorePatterns: ['/node_modules/'],
  collectCoverage: true,
  collectCoverageFrom: ['src/**/*.ts(x)'],
  setupFilesAfterEnv: ['<rootDir>/.jest/setup.ts'],
  modulePaths: [
    '<rootDir>/src/',
    '<rootDir>/.jest',
    '<rootDir>/',
    '<rootDir>/../'
  ],
  reporters: ['default', '<rootDir>/node_modules/jest-html-reporter']
}

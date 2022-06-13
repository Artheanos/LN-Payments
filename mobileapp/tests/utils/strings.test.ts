import { capitalize, isValidUrl } from 'utils/strings'

describe('capitalize', () => {
  const cases = [
    ['this is a test', 'This is a test'],
    ['AAA', 'AAA'],
    ['a', 'A'],
  ]

  it.each(cases)('capitalizes a string', (input, expected) => {
    expect(capitalize(input)).toBe(expected)
  })
})

describe('isValidUrl', () => {
  const cases: [string, boolean][] = [
    ['https://google.pl', true],
    ['h://a', true],
    ['http://localhost:3000', true],
    ['localhost:3000', false],
  ]

  it.each(cases)('validate %s %i', (input, expected) => {
    expect(isValidUrl(input)).toBe(expected)
  })
})

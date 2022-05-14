import { capitalize } from '../../utils/strings'

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

import { toHexBytes, toHexString } from 'utils/hex'
import { Buffer } from 'buffer'

describe('hex utils', () => {
  const cases = [
    [Buffer.of(21, 37, 12, 44, 88), '15250c2c58'],
    [Buffer.of(), ''],
    [Buffer.of(1), '01'],
  ]

  it.each(cases)('should convert array to string', (input, expected) => {
    expect(toHexString(input as Buffer)).toBe(expected)
  })

  it.each(cases)('should convert string to array', (expected, input) => {
    expect(toHexBytes(input as string)).toStrictEqual(expected)
  })
})

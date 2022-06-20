import { toHexBytes, toHexString } from 'utils/hex'
import { Buffer } from 'buffer'

describe('hex utils', () => {
  const cases = [
    [Buffer.of(21, 37, 12, 44, 88), '15250c2c58'],
    [Buffer.of(), ''],
    [Buffer.of(1), '01'],
    [
      Buffer.of(
        191,
        70,
        68,
        236,
        86,
        1,
        238,
        134,
        86,
        29,
        144,
        23,
        239,
        164,
        115,
        161,
        189,
        123,
        134,
        203,
        17,
        121,
        43,
        21,
        222,
        120,
        158,
        12,
        149,
        83,
        132,
        148,
      ),
      'bf4644ec5601ee86561d9017efa473a1bd7b86cb11792b15de789e0c95538494',
    ],
  ]

  it.each(cases)('should convert array to string', (input, expected) => {
    expect(toHexString(input as Buffer)).toBe(expected)
  })

  it.each(cases)('should convert string to array', (expected, input) => {
    expect(toHexBytes(input as string)).toStrictEqual(expected)
  })
})

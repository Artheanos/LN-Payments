import { datify, millisecondsToClock } from 'utils/time'

describe('datify', () => {
  describe('when input is valid date', () => {
    const validDates = [
      '2022-02-15T16:24:42.457760Z',
      '2022-02-16T21:30:19.447Z'
    ]

    it.each(validDates)('returns date', (dateString) => {
      const input = { dateString }
      expect(datify(input).dateString).toStrictEqual(new Date(dateString))
    })
  })

  describe('when input is not valid date', () => {
    const invalidDates = ['', 'test', '2022-02-02T16:02:03.123']

    it.each(invalidDates)('returns input', (dateString) => {
      const input = { dateString }
      expect(datify(input).dateString).toBe(dateString)
    })
  })

  describe('when input is a valid regex but not a valid date', () => {
    it('returns invalid date', () => {
      expect(
        datify({
          dateString: '1111-99-99T99:99:99.999999Z'
        }).dateString.toString()
      ).toEqual('Invalid Date')
    })
  })
})

describe('millisecondsToClock', () => {
  const cases = [
    [0, '00:00'],
    [999, '00:00'],
    [1_000, '00:01'],
    [1_001, '00:01'],
    [879_000, '14:39'],
    [-5_000, '59:55']
  ]
  it.each(cases)('displays proper time', (input, expectedOutput) => {
    expect(millisecondsToClock(input as number)).toEqual(expectedOutput)
  })
})

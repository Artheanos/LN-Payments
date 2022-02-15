import { datify } from '../../src/utils/time'

describe('datify', () => {
  describe('when input is valid date', () => {
    const validDates = [
      '2022-02-15T16:24:42.457760Z',
      '1111-99-99T99:99:99.999999Z'
    ]

    it.each(validDates)('returns date', (dateString) => {
      const input = { dateString }
      expect(datify(input).dateString).toEqual(new Date(dateString))
    })
  })

  describe('when input is not valid date', () => {
    const invalidDates = ['', 'test', '2022-02-02T16:02:03.123']

    it.each(invalidDates)('returns input', (dateString) => {
      const input = { dateString }
      expect(datify(input).dateString).toBe(dateString)
    })
  })
})

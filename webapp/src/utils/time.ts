import { useEffect, useMemo, useState } from 'react'

export const millisecondsToClock = (milliseconds: number) => {
  return new Date(milliseconds).toISOString().substring(14, 19)
}

const fromNow = (milliseconds: number) => {
  const result = new Date()
  result.setMilliseconds(result.getMilliseconds() + milliseconds)
  return result
}

export const useCountdown = (milliseconds: number, onTimeout: () => void) => {
  const deadline = useMemo(() => fromNow(milliseconds), [])

  const [timeLeft, setTimeLeft] = useState(
    deadline.valueOf() - new Date().valueOf() - 1
  )

  useEffect(() => {
    if (timeLeft > 0) {
      const interval = setTimeout(() => {
        let newTimeLeft = deadline.valueOf() - new Date().valueOf()
        if (newTimeLeft < 0) {
          newTimeLeft = 0
        }
        setTimeLeft(newTimeLeft)
      }, 1000)
      return () => clearTimeout(interval)
    } else {
      onTimeout()
    }
  }, [onTimeout, deadline, timeLeft])

  return timeLeft
}

const API_DATE_REGEX = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{6}Z$/

export const datify = <T extends Record<string, string | Date | never>>(
  object: T
) => {
  Object.entries(object).forEach(([key, value]) => {
    if (typeof value === 'string' && value.match(API_DATE_REGEX)) {
      ;(object[key] as Date) = new Date(value)
    }
  })

  return object
}

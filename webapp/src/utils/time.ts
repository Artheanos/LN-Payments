import { useEffect, useState } from 'react'

export const millisecondsToClock = (milliseconds: number) => {
  return new Date(milliseconds).toISOString().substring(14, 19)
}

export const useCountdown = (deadline?: Date, onTimeout?: () => void) => {
  if (!deadline) {
    deadline = new Date()
  }

  const [timeLeft, setTimeLeft] = useState(
    deadline.valueOf() - new Date().valueOf() - 1
  )

  useEffect(() => {
    if (timeLeft > 0) {
      const interval = setTimeout(() => {
        let newTimeLeft = deadline!.valueOf() - new Date().valueOf()
        if (newTimeLeft < 0) {
          newTimeLeft = 0
        }
        setTimeLeft(newTimeLeft)
      }, 1000)
      return () => clearTimeout(interval)
    } else {
      onTimeout?.()
    }
  }, [onTimeout, deadline, timeLeft])

  return timeLeft
}

const API_DATE_REGEX = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d+Z$/

export const datify = <T>(_object: T) => {
  const object = _object as Record<string, unknown>
  Object.entries(object).forEach(([key, value]) => {
    if (typeof value === 'string' && value.match(API_DATE_REGEX)) {
      ;(object[key] as Date) = new Date(value)
    }
  })

  return object as T
}

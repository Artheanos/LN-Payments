import { useCallback, useState } from 'react'
import { datify } from 'utils/time'

export const getLocalJson = (key: string) => {
  const item = localStorage.getItem(key)
  try {
    return item ? datify(JSON.parse(item)) : undefined
  } catch (e) {
    return undefined
  }
}

export const setLocalJson = (key: string, value: unknown) => {
  localStorage.setItem(key, JSON.stringify(value))
}

export const useLocalStorage = <S = undefined, T = S | undefined>(
  key: string
): [T, (v: T) => void] => {
  const [state, setState] = useState<T>(getLocalJson(key))
  const setStateWrapper = useCallback(
    (newState: T) => {
      setLocalJson(key, newState)
      setState(newState)
    },
    [setState, key]
  )

  return [state, setStateWrapper]
}

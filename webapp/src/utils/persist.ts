import { Dispatch, SetStateAction, useEffect, useState } from 'react'

const getLocalJson = (key: string) => {
  const item = localStorage.getItem(key)
  return item ? JSON.parse(item) : undefined
}

const setLocalJson = (key: string, value: unknown) => {
  localStorage.setItem(key, JSON.stringify(value))
}

export const useLocalStorage = <S = undefined>(
  key: string
): [S, Dispatch<SetStateAction<S>>] => {
  const state = useState<S>(getLocalJson(key))
  const [stateValue] = state

  useEffect(() => {
    setLocalJson(key, stateValue)
  }, [key, stateValue])

  return state
}

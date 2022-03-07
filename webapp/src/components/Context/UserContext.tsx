import React, { useCallback, useEffect, useMemo, useState } from 'react'

import { api } from 'api'
import { useLocalStorage } from 'utils/persist'

type ContextType = {
  user?: User
  setUser: (user: User) => void
  token?: string
  setToken: (token: string) => void
  loading: boolean
  isValid: boolean
  tryRefreshingToken: () => void
}

const defaultValue: ContextType = {
  user: undefined,
  setUser: () => {},
  token: undefined,
  setToken: () => {},
  loading: true,
  isValid: false,
  tryRefreshingToken: () => {}
}

export const UserContext = React.createContext(defaultValue)

export const UserProvider: React.FC = ({ children }) => {
  const [user, setUser] = useLocalStorage<User>('user')
  const [token, setToken] = useLocalStorage<string>('token')
  const [loading, setLoading] = useState(true)
  const isValid = useMemo(() => Boolean(user && token), [user, token])

  const tryRefreshingToken = useCallback(() => {
    setLoading(true)
    api.auth
      .refreshToken()
      .then(({ data }) => {
        setToken(data?.token || '')
      })
      .finally(() => setLoading(false))
  }, [setLoading, setToken])

  useEffect(() => tryRefreshingToken(), [tryRefreshingToken])

  return (
    <UserContext.Provider
      value={{
        user,
        setUser,
        token,
        setToken,
        loading,
        isValid,
        tryRefreshingToken
      }}
    >
      {children}
    </UserContext.Provider>
  )
}

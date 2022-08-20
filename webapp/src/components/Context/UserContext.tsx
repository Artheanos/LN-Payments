import React, { useCallback, useEffect, useMemo, useState } from 'react'

import { LocalKey } from 'constants/LocalKey'
import { REFRESH_TOKEN_INTERVAL } from 'constants/miscellaneous'
import { Role, User } from 'webService/interface/user'
import { api } from 'webService/requests'
import { useLocalStorage } from 'utils/persist'

type ContextType = {
  user?: User
  setUser: (user: User) => void
  token?: string
  setToken: (token: string) => void
  loading: boolean
  isLoggedIn: boolean
  hasAccount: boolean
  logout: () => void
  login: (user: User, token: string) => void
  tryRefreshingToken: () => void
}

export const defaultValue: ContextType = {
  user: undefined,
  setUser: () => {},
  token: undefined,
  setToken: () => {},
  loading: true,
  isLoggedIn: false,
  logout: () => {},
  login: () => {},
  hasAccount: false,
  tryRefreshingToken: () => {}
}

export const UserContext = React.createContext(defaultValue)

export const UserProvider: React.FC = ({ children }) => {
  const [user, setUser] = useLocalStorage<User>(LocalKey.USER)
  const [token, setToken] = useLocalStorage<string>(LocalKey.TOKEN)
  const [loading, setLoading] = useState(true)
  const isLoggedIn = useMemo(() => Boolean(user && token), [user, token])
  const hasAccount = useMemo(
    () => isLoggedIn && user?.role !== Role.TEMPORARY,
    [isLoggedIn, user?.role]
  )

  const logout = useCallback(() => {
    setToken(undefined)
    setUser(undefined)
    localStorage.setItem(LocalKey.TRANSACTION_TOKENS, '')
  }, [setToken, setUser])

  const login = useCallback(
    (user: User, token: string) => {
      setUser(user)
      setToken(token)
    },
    [setToken, setUser]
  )

  const tryRefreshingToken = useCallback(() => {
    setLoading(true)
    api.auth
      .refreshToken(5000)
      .then(({ data }) => {
        if (data) {
          setToken(data.token)
        } else {
          logout()
        }
      })
      .catch(logout)
      .finally(() => {
        setTimeout(() => tryRefreshingToken(), REFRESH_TOKEN_INTERVAL)
        setLoading(false)
      })
  }, [logout, setToken])

  useEffect(() => tryRefreshingToken(), [tryRefreshingToken])

  return (
    <UserContext.Provider
      value={{
        user,
        setUser,
        token,
        setToken,
        loading,
        isLoggedIn,
        logout,
        login,
        tryRefreshingToken,
        hasAccount
      }}
    >
      {children}
    </UserContext.Provider>
  )
}

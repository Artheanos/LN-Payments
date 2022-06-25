/* eslint-disable @typescript-eslint/no-empty-function */

import React, { createContext, useCallback, useState } from 'react'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { LocalKey } from 'constants/LocalKey'

/**
 * Interface defining types for user context.
 */
interface UserI {
  email: string | null
  hostUrl: string | null
  notificationChannelId: string | null
  privateKey: Buffer | null
  publicKey: Buffer | null
  token: string | null
  uploadKeys: boolean | null
}

/**
 * Defined empty user, used as the initial state value.
 */
export const EMPTY_USER = Object.freeze({
  email: null,
  hostUrl: null,
  notificationChannelId: null,
  privateKey: null,
  publicKey: null,
  token: null,
  uploadKeys: false,
})

/**
 * Context containing details about the current user. Data is updated on log in. By default,
 * initialized with {@see EMPTY_USER}.
 */
export const UserContext = createContext<{
  user: UserI
  setUser: (user: UserI) => void
  updateUser: (params: Partial<UserI>) => void
  logoutUser: () => void
}>({
  user: EMPTY_USER,
  setUser: () => {},
  updateUser: () => {},
  logoutUser: () => {},
})

/**
 * Provides an user context.
 */
export const UserContextProvider: React.FC = ({ children }) => {
  const [user, setUser] = useState<UserI>(EMPTY_USER)
  const updateUser = useCallback(
    (params: Partial<UserI>) => {
      setUser((prevUser) => ({ ...prevUser, ...params }))
    },
    [setUser],
  )
  const logoutUser = useCallback(() => {
    AsyncStorage.multiSet([
      [LocalKey.TOKEN, ''],
      [LocalKey.EMAIL, ''],
    ]).then(() => {
      updateUser({
        email: null,
        token: null,
        privateKey: null,
        publicKey: null,
      })
    })
  }, [updateUser])

  return (
    <UserContext.Provider
      value={{
        user,
        setUser,
        updateUser,
        logoutUser,
      }}
    >
      {children}
    </UserContext.Provider>
  )
}

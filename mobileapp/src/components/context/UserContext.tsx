/* eslint-disable @typescript-eslint/no-empty-function */

import React, { createContext, useCallback, useEffect, useState } from 'react'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { LocalKey } from 'constants/LocalKey'
import { requests } from 'webService/requests'

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
  userLoading: boolean
}>({
  user: EMPTY_USER,
  setUser: () => {},
  updateUser: () => {},
  logoutUser: () => {},
  userLoading: true,
})

/**
 * Provides a user context.
 * Handles loading data from persistent storage to temporary storage
 */
export const UserContextProvider: React.FC = ({ children }) => {
  const [user, setUser] = useState<UserI>(EMPTY_USER)
  const [userLoading, setUserLoading] = useState(true)

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
        uploadKeys: false,
      })
    })
  }, [updateUser])

  /**
   * Obtains user data from the local storage. When the user is turns on the application, and he was already
   * logged in, his data will be loaded from the memory.
   */
  const loadUserFromStorage = useCallback(async () => {
    const keys = [
      LocalKey.TOKEN,
      LocalKey.EMAIL,
      LocalKey.HOST_URL,
      LocalKey.NOTIFICATION_CHANNEL,
    ]

    const partialUser: Record<string, unknown> = {}
    const storageKeyValues = await AsyncStorage.multiGet(keys)

    for (const [key, value] of storageKeyValues) {
      if (value === null) continue

      switch (key) {
        case LocalKey.TOKEN:
          requests.setToken(value)
          partialUser.token = value
          break
        case LocalKey.EMAIL:
          partialUser.email = value
          break
        case LocalKey.HOST_URL:
          partialUser.hostUrl = value
          requests.host = value
          break
        case LocalKey.NOTIFICATION_CHANNEL:
          partialUser.notificationChannelId = value
          break
      }
    }

    updateUser(partialUser)
  }, [updateUser])

  /**
   * Loads from the memory users' private key.
   */
  const loadKeysFromStorage = useCallback(
    async (email: string) => {
      const value = await AsyncStorage.getItem(email)
      if (!value) {
        updateUser({ uploadKeys: true })
        return
      }

      const keyPair = JSON.parse(value)
      updateUser(keyPair)
    },
    [updateUser],
  )

  /**
   * Listens for the invocation of load user method invocation. Then invocation is done, sets loading flag to false.
   */
  useEffect(() => {
    loadUserFromStorage().then(() => setUserLoading(false))
  }, [loadUserFromStorage])

  /**
   * Invokes load keys method, when user is logged in.
   */
  useEffect(() => {
    if (user.email) {
      setUserLoading(true)
      loadKeysFromStorage(user.email).then(() => setUserLoading(false))
    }
  }, [loadKeysFromStorage, user.email])

  return (
    <UserContext.Provider
      value={{
        user,
        setUser,
        updateUser,
        logoutUser,
        userLoading,
      }}
    >
      {children}
    </UserContext.Provider>
  )
}

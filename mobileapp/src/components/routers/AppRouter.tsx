import React, { useCallback, useContext, useEffect, useState } from 'react'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { Spinner } from 'native-base'

import { LocalKey } from 'constants/LocalKey'
import { SignedInRouter } from './SignedInRouter'
import { SingedOutRouter } from './SignedOutRouter'
import { UserContext } from 'components/context/UserContext'
import { requests } from 'webService/requests'
import notifee, { EventType, Event } from '@notifee/react-native'
import { Linking } from 'react-native'
import linking from 'res/linking'

/**
 * Main application router, that determines between Signed in and Signed out flows. It also handles
 * the initial setup of the application.
 */
export const AppRouter: React.FC = () => {
  const { user, updateUser } = useContext(UserContext)
  const [loading, setLoading] = useState(true)

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
    loadUserFromStorage().then(() => setLoading(false))
  }, [loadUserFromStorage])

  /**
   * Invokes load keys method, when user is logged in.
   */
  useEffect(() => {
    if (user.email) {
      setLoading(true)
      loadKeysFromStorage(user.email).then(() => setLoading(false))
    }
  }, [loadKeysFromStorage, user.email])

  /**
   * Sets up push notification listener, when application is launched.
   */
  useEffect(() => {
    const notificationListener = async ({ type, detail }: Event) => {
      if (type === EventType.PRESS) {
        await Linking.openURL(
          linking.screens['Notification Details'](detail.notification?.id),
        )
      }
    }

    notifee.onForegroundEvent(notificationListener)
    notifee.onBackgroundEvent(notificationListener)
  }, [])

  /**
   * When data is being loaded, display a spinner.
   */
  if (loading) return <Spinner />

  /**
   * Returns router for proper flow, based on user log in status,
   */
  return user.token && user.privateKey ? (
    <SignedInRouter />
  ) : (
    <SingedOutRouter />
  )
}

import React, { useCallback, useContext, useEffect, useState } from 'react'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { Spinner } from 'native-base'

import { LocalKey } from 'constants/LocalKey'
import { SignedInRouter } from './SignedInRouter'
import { SingedOutRouter } from './SignedOutRouter'
import { UserContext } from 'components/context/UserContext'
import { requests } from 'webService/requests'

export const AppRouter: React.FC = () => {
  const { user, updateUser } = useContext(UserContext)
  const [loading, setLoading] = useState(true)

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

  useEffect(() => {
    loadUserFromStorage().then(() => setLoading(false))
  }, [loadUserFromStorage])

  useEffect(() => {
    if (user.email) {
      setLoading(true)
      loadKeysFromStorage(user.email).then(() => setLoading(false))
    }
  }, [loadKeysFromStorage, user.email])

  if (loading) return <Spinner />

  return user.token && user.privateKey ? (
    <SignedInRouter />
  ) : (
    <SingedOutRouter />
  )
}

import React, { useCallback, useContext, useEffect, useState } from 'react'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { Spinner } from 'native-base'

import { UserContext } from 'components/context/UserContext'
import { SignedInRouter } from './SignedInRouter'
import { SingedOutRouter } from './SignedOutRouter'

export const AppRouter: React.FC = () => {
  const { user, updateUser } = useContext(UserContext)
  const [loading, setLoading] = useState(true)

  const loadUserFromStorage = useCallback(async () => {
    const keys = ['token', 'email']

    const keyValues = await AsyncStorage.multiGet(keys)
    for (const [key, value] of keyValues) {
      const partialUser: Record<string, unknown> = {}
      if (key === 'token') {
        partialUser.token = value
      } else if (key === 'email') {
        partialUser.email = value
      }
      updateUser(partialUser)
    }
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

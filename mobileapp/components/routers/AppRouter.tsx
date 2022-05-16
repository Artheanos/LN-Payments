import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useEffect, useState } from 'react'
import { Spinner } from 'native-base'

import { LocalKey } from 'constants/LocalKey'
import { SignedInRouter } from './SignedInRouter'
import { SignedOutRouter } from './SignedOutRouter'
import { UserContext } from 'components/context/UserContext'

export const AppRouter: React.FC = () => {
  const { token, setToken } = useContext(UserContext)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    AsyncStorage.getItem(LocalKey.TOKEN).then((token) => {
      setToken(token)
      setLoading(false)
    })
  }, [setToken])

  if (loading) return <Spinner />

  return token ? <SignedInRouter /> : <SignedOutRouter />
}

import React, { useContext, useEffect, useState } from 'react'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { Spinner } from 'native-base'

import { UserContext } from 'components/context/UserContext'
import { SignedInRouter } from './SignedInRouter'
import { SingedOutRouter } from './SignedOutRouter'

export const AppRouter: React.FC = () => {
  const { token, setToken, privateKey, setPrivateKey, setPublicKey } =
    useContext(UserContext)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    AsyncStorage.multiGet(['token', 'privateKey', 'publicKey']).then(
      (pairs) => {
        for (const [key, value] of pairs) {
          if (key === 'token') setToken(value)
          else if (key === 'privateKey') setPrivateKey(value)
          else if (key === 'publicKey') setPublicKey(value)
        }
        setLoading(false)
      },
    )
  }, [setPrivateKey, setPublicKey, setToken])

  if (loading) return <Spinner />

  return token && privateKey ? <SignedInRouter /> : <SingedOutRouter />
}

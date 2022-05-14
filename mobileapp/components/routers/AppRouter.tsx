import React, { useContext, useEffect, useState } from 'react'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { UserContext } from '../context/UserContext'
import { Spinner } from 'native-base'
import { SignedInRouter } from './SignedInRouter'
import { SingedOutRouter } from './SignedOutRouter'

export const AppRouter: React.FC = () => {
  const { token, setToken } = useContext(UserContext)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    AsyncStorage.getItem('token').then((token) => {
      setToken(token)
      setLoading(false)
    })
  }, [setToken])

  if (loading) return <Spinner />

  return token ? <SignedInRouter /> : <SingedOutRouter />
}

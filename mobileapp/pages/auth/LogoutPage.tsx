import React, { useContext, useEffect } from 'react'
import { Text } from 'native-base'
import { UserContext } from '../../components/context/UserContext'
import AsyncStorage from '@react-native-async-storage/async-storage'

export const LogoutPage: React.FC = () => {
  const { setToken } = useContext(UserContext)

  useEffect(() => {
    AsyncStorage.setItem('token', '').then(() => setToken(null))
  }, [setToken])

  return <Text>Logging out...</Text>
}

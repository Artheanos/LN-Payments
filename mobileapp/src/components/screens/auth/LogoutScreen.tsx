import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useEffect } from 'react'
import { Text } from 'native-base'

import { EMPTY_USER, UserContext } from 'components/context/UserContext'

export const LogoutScreen: React.FC = () => {
  const { updateUser } = useContext(UserContext)

  useEffect(() => {
    AsyncStorage.multiSet([
      ['token', ''],
      ['email', ''],
    ]).then(() => updateUser(EMPTY_USER))
  }, [updateUser])

  return <Text>Logging out...</Text>
}

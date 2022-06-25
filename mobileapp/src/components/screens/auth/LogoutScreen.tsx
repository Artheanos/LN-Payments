import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useEffect } from 'react'
import { Text } from 'native-base'

import { LocalKey } from 'constants/LocalKey'
import { UserContext } from 'components/context/UserContext'
import R from 'res/R'

/**
 * Component displayed when logout in progress.
 */
export const LogoutScreen: React.FC = () => {
  const { updateUser } = useContext(UserContext)

  /**
   * Changes local storage values back to the default values. It makes the application see, that
   * user has logged off.
   */
  useEffect(() => {
    AsyncStorage.multiSet([
      [LocalKey.TOKEN, ''],
      [LocalKey.EMAIL, ''],
    ]).then(() =>
      updateUser({
        email: null,
        token: null,
        privateKey: null,
        publicKey: null,
      }),
    )
  }, [updateUser])

  return <Text>{R.strings.logout.action}</Text>
}

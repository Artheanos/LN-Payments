import React, { useContext, useEffect } from 'react'
import { Text } from 'native-base'

import { UserContext } from 'components/context/UserContext'
import R from 'res/R'

/**
 * Component displayed when logout in progress.
 */
export const LogoutScreen: React.FC = () => {
  const { logoutUser } = useContext(UserContext)

  /**
   * Changes local storage values back to the default values. It makes the application see, that
   * user has logged off.
   */
  useEffect(() => {
    logoutUser()
  }, [logoutUser])

  return <Text>{R.strings.logout.action}</Text>
}

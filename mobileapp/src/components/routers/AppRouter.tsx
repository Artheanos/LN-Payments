import React, { useContext, useEffect } from 'react'
import { Spinner } from 'native-base'

import { SignedInRouter } from './SignedInRouter'
import { SignedOutRouter } from './SignedOutRouter'
import { UserContext } from 'components/context/UserContext'
import notifee, { EventType, Event } from '@notifee/react-native'
import { Linking } from 'react-native'
import linking from 'res/linking'

/**
 * Main application router, that determines between Signed in and Signed out flows.
 */
export const AppRouter: React.FC = () => {
  const { user, userLoading } = useContext(UserContext)

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
  if (userLoading) return <Spinner />

  /**
   * Returns router for proper flow, based on user log in status,
   */
  return user.token && user.privateKey ? (
    <SignedInRouter />
  ) : (
    <SignedOutRouter />
  )
}

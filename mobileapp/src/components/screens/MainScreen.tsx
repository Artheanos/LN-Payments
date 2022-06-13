import React from 'react'
import { StackNavigationProp } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import { NotificationsListScreen } from 'components/screens/notification/NotificationsListScreen'

/**
 * Functional component responsible for main screen of the application. Intended to be used only from navigators.
 *
 * @param navigation Nagivation prop passed from router
 */
export const MainScreen: React.FC<{
  navigation: StackNavigationProp<SignInRouterProps>
}> = ({ navigation }) => {
  return <NotificationsListScreen navigation={navigation} />
}

import React from 'react'
import { StackNavigationProp } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import { NotificationListScreen } from 'components/screens/notification/NotificationListScreen/NotificationListScreen'

/**
 * Functional component responsible for main screen of the application. Intended to be used only from navigators.
 *
 * @param navigation  Navigation prop passed from router
 */
export const MainScreen: React.FC<{
  navigation: StackNavigationProp<SignInRouterProps>
}> = ({ navigation }) => {
  return <NotificationListScreen navigation={navigation} />
}

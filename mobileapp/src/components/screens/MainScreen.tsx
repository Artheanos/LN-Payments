import React from 'react'
import { StackNavigationProp } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import { NotificationsListScreen } from 'components/screens/notification/NotificationsListScreen'

export const MainScreen: React.FC<{
  navigation: StackNavigationProp<SignInRouterProps>
}> = ({ navigation }) => {
  return <NotificationsListScreen navigation={navigation} />
}

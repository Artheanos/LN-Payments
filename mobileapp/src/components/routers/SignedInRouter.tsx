import React from 'react'
import { NotificationDetailScreen } from 'components/screens/notification/NotificationDetailScreen'
import { DrawerRouter } from 'components/routers/DrawerRouter'
import { createStackNavigator } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'

const Stack = createStackNavigator<SignInRouterProps>()

export const SignedInRouter: React.FC = () => {
  return (
    <Stack.Navigator>
      <Stack.Screen
        name={'Drawer'}
        component={DrawerRouter}
        options={{ headerShown: false }}
      />
      <Stack.Screen
        name={'Notification details'}
        component={NotificationDetailScreen}
      />
    </Stack.Navigator>
  )
}

import React from 'react'
import { NotificationDetailScreen } from 'components/screens/notification/NotificationDetailScreen'
import { DrawerRouter } from 'components/routers/DrawerRouter'
import { createStackNavigator } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import R from 'res/R'

const Stack = createStackNavigator<SignInRouterProps>()

export const SignedInRouter: React.FC = () => {
  return (
    <Stack.Navigator>
      <Stack.Screen
        name={R.routes.drawer}
        component={DrawerRouter}
        options={{ headerShown: false }}
      />
      <Stack.Screen
        name={R.routes.notificationDetails}
        component={NotificationDetailScreen}
      />
    </Stack.Navigator>
  )
}

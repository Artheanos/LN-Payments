import React from 'react'
import { NotificationDetailScreen } from 'components/screens/notification/NotificationDetailScreen/NotificationDetailScreen'
import { DrawerRouter } from 'components/routers/DrawerRouter'
import { createStackNavigator } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import R from 'res/R'
import { NotificationResultScreen } from 'components/screens/notification/NotificationResultScreen'

const Stack = createStackNavigator<SignInRouterProps>()

/**
 * Main application router for logged users. Includes drawer and other screens that must be
 * always on top when invoked.
 */
export const SignedInRouter: React.FC = () => {
  return (
    <SignedInContextProvider>
      <Stack.Screen
        name={R.routes.drawer}
        component={DrawerRouter}
        options={{ headerShown: false }}
      />
      <Stack.Screen
        name={R.routes.notificationDetails}
        component={NotificationDetailScreen}
      />
      <Stack.Screen
        name={R.routes.outcome}
        component={NotificationResultScreen}
        options={{
          presentation: 'modal',
          headerShown: false,
        }}
      />
    </SignedInContextProvider>
  )
}

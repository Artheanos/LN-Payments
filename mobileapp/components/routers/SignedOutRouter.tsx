import React from 'react'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { LoginScreen } from 'components/screens/auth/LoginScreen'
import { useTranslation } from 'react-i18next'

const Stack = createNativeStackNavigator()

export const SignedOutRouter: React.FC = () => {
  const { t } = useTranslation()

  return (
    <>
      {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
      {/* @ts-ignore */}
      <Stack.Navigator>
        <Stack.Screen
          name="Login"
          options={{ title: t('title') }}
          component={LoginScreen}
        />
      </Stack.Navigator>
    </>
  )
}

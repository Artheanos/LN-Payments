import React from 'react'
import { LoginScreen } from 'components/screens/auth/LoginScreen'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { KeyUploadScreen } from 'components/screens/auth/KeyUploadScreen'

const Stack = createNativeStackNavigator()

export const SingedOutRouter: React.FC = () => {
  return (
    <>
      {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
      {/* @ts-ignore */}
      <Stack.Navigator>
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="Keys" component={KeyUploadScreen} />
      </Stack.Navigator>
    </>
  )
}

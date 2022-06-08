import React, { useContext } from 'react'
import { LoginScreen } from 'components/screens/auth/LoginScreen'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { KeyUploadScreen } from 'components/screens/auth/KeyUploadScreen'
import { UserContext } from 'components/context/UserContext'

const Stack = createNativeStackNavigator()

export const SingedOutRouter: React.FC = () => {
  const { user } = useContext(UserContext)

  return (
    <>
      {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
      {/* @ts-ignore */}
      <Stack.Navigator>
        {user.uploadKeys ? (
          <Stack.Screen name="Keys" component={KeyUploadScreen} />
        ) : (
          <Stack.Screen name="Login" component={LoginScreen} />
        )}
      </Stack.Navigator>
    </>
  )
}
import React, { useContext } from 'react'
import { LoginScreen } from 'components/screens/auth/LoginScreen'
import { createNativeStackNavigator } from '@react-navigation/native-stack'
import { KeyUploadScreen } from 'components/screens/auth/KeyUploadScreen'
import { UserContext } from 'components/context/UserContext'
import R from 'res/R'

const Stack = createNativeStackNavigator()

/**
 * Application router for non-logged users. Automatically navigates the user between login and key upload
 * screens.
 */
export const SignedOutRouter: React.FC = () => {
  const { user } = useContext(UserContext)

  return (
    <>
      {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
      {/* @ts-ignore */}
      <Stack.Navigator>
        {user.uploadKeys ? (
          <Stack.Screen name={R.routes.keys} component={KeyUploadScreen} />
        ) : (
          <Stack.Screen name={R.routes.login} component={LoginScreen} />
        )}
      </Stack.Navigator>
    </>
  )
}

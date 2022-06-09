import 'react-native-gesture-handler'

import * as React from 'react'
import { NativeBaseProvider } from 'native-base'
import { NavigationContainer } from '@react-navigation/native'

import { UserContextProvider } from 'components/context/UserContext'
import { AppRouter } from 'components/routers/AppRouter'

export default function App() {
  return (
    <NativeBaseProvider>
      <UserContextProvider>
        <NavigationContainer>
          <AppRouter />
        </NavigationContainer>
      </UserContextProvider>
    </NativeBaseProvider>
  )
}

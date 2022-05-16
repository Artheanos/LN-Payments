import 'react-native-gesture-handler'
import '../i18n'

import React from 'react'
import { NativeBaseProvider } from 'native-base'
import { NavigationContainer } from '@react-navigation/native'

import { UserContextProvider } from './context/UserContext'
import { AppRouter } from './routers/AppRouter'

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

import 'react-native-gesture-handler'

import * as React from 'react'
import { extendTheme, NativeBaseProvider } from 'native-base'
import { NavigationContainer } from '@react-navigation/native'
import colors from 'native-base/src/theme/base/colors'

import { UserContextProvider } from 'components/context/UserContext'
import { AppRouter } from 'components/routers/AppRouter'

const theme = extendTheme({
  colors: {
    primary: colors.purple,
  },
})

export default function App() {
  return (
    <NativeBaseProvider theme={theme}>
      <UserContextProvider>
        <NavigationContainer>
          <AppRouter />
        </NavigationContainer>
      </UserContextProvider>
    </NativeBaseProvider>
  )
}

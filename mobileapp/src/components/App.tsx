import 'react-native-gesture-handler'
import 'text-encoding'
import * as React from 'react'
import { extendTheme, NativeBaseProvider } from 'native-base'
import { NavigationContainer } from '@react-navigation/native'
import colors from 'native-base/src/theme/base/colors'
import { LogBox } from 'react-native'

import { UserContextProvider } from 'components/context/UserContext'
import { AppRouter } from 'components/routers/AppRouter'
import R from 'res/R'

LogBox.ignoreAllLogs()

/**
 * Defines the color scheme used in the application.
 */
const theme = extendTheme({
  colors: {
    primary: colors.purple,
  },
})

/**
 * Main component that is the entry point to the application.
 * Contains all context providers and navigation utilities.
 */
export default function App() {
  return (
    <NativeBaseProvider theme={theme}>
      <UserContextProvider>
        <NavigationContainer linking={linking}>
          <AppRouter />
        </NavigationContainer>
      </UserContextProvider>
    </NativeBaseProvider>
  )
}

/**
 * Defines screen linking in the application navigation.
 */
const linking = {
  prefixes: [R.linking.prefix],
  config: {
    screens: R.linking.configScreens,
  },
}

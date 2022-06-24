import 'react-native-gesture-handler'
import 'text-encoding'
import * as React from 'react'
import { extendTheme, NativeBaseProvider } from 'native-base'
import { NavigationContainer } from '@react-navigation/native'
import colors from 'native-base/src/theme/base/colors'

import { UserContextProvider } from 'components/context/UserContext'
import { AppRouter } from 'components/routers/AppRouter'
import R from 'res/R'

const theme = extendTheme({
  colors: {
    primary: colors.purple,
  },
})

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

const linking = {
  prefixes: [R.linking.prefix],
  config: {
    screens: R.linking.configScreens,
  },
}

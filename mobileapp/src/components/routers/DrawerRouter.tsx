import React from 'react'
import { MainScreen } from 'components/screens/MainScreen'
import { LogoutScreen } from 'components/screens/auth/LogoutScreen'
import { createDrawerNavigator } from '@react-navigation/drawer'

const Drawer = createDrawerNavigator()

export const DrawerRouter: React.FC = () => {
  return (
    <Drawer.Navigator>
      <Drawer.Screen name="Home" component={MainScreen} />
      <Drawer.Screen name="Logout" component={LogoutScreen} />
    </Drawer.Navigator>
  )
}

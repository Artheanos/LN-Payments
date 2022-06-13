import React from 'react'
import { MainScreen } from 'components/screens/MainScreen'
import { LogoutScreen } from 'components/screens/auth/LogoutScreen'
import { createDrawerNavigator } from '@react-navigation/drawer'
import R from 'res/R'

const Drawer = createDrawerNavigator()

/**
 * Router responsible for drawer routes.
 */
export const DrawerRouter: React.FC = () => {
  return (
    <Drawer.Navigator>
      <Drawer.Screen name={R.routes.home} component={MainScreen} />
      <Drawer.Screen name={R.routes.logout} component={LogoutScreen} />
    </Drawer.Navigator>
  )
}

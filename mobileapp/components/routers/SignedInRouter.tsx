import React from 'react'
import { createDrawerNavigator } from '@react-navigation/drawer'
import { MainScreen } from '../screens/MainScreen'
import { LogoutScreen } from '../screens/auth/LogoutScreen'

const Drawer = createDrawerNavigator()

export const SignedInRouter: React.FC = () => {
  return (
    <>
      {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
      {/* @ts-ignore */}
      <Drawer.Navigator>
        <Drawer.Screen name="Home" component={MainScreen} />
        <Drawer.Screen name="Logout" component={LogoutScreen} />
      </Drawer.Navigator>
    </>
  )
}

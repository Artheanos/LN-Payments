import React from 'react'
import { createDrawerNavigator } from '@react-navigation/drawer'
import { MainPage } from '../../pages/MainPage'
import { LogoutPage } from '../../pages/auth/LogoutPage'

const Drawer = createDrawerNavigator()

export const SignedInRouter: React.FC = () => {
  return (
    <>
      {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
      {/* @ts-ignore */}
      <Drawer.Navigator>
        <Drawer.Screen name="MainPage" component={MainPage} />
        <Drawer.Screen name="Logout" component={LogoutPage} />
      </Drawer.Navigator>
    </>
  )
}

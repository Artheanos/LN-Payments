import React from 'react'
import { createDrawerNavigator } from '@react-navigation/drawer'

import { MainScreen } from 'components/screens/MainScreen'
import { LogoutScreen } from 'components/screens/auth/LogoutScreen'
import { DrawerLayout } from 'components/layouts/DrawerLayout'
import { useTranslation } from 'react-i18next'

export const SignedInRouter: React.FC = () => {
  const { t } = useTranslation('sidebar')

  return (
    <>
      {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
      {/* @ts-ignore */}
      <Drawer.Navigator>
        <Drawer.Screen
          name="Home"
          options={{ title: t('home') }}
          component={componentFactory(MainScreen)}
        />
        <Drawer.Screen
          name="Logout"
          options={{ title: t('logout') }}
          component={componentFactory(LogoutScreen)}
        />
      </Drawer.Navigator>
    </>
  )
}

const Drawer = createDrawerNavigator()

const componentFactory = (Child: React.FC): React.FC => {
  const Result: React.FC = () => (
    <DrawerLayout>
      <Child />
    </DrawerLayout>
  )
  Result.displayName = `${DrawerLayout.displayName}${Child.displayName}`
  return Result
}

import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useEffect } from 'react'
import { Text } from 'native-base'
import { useTranslation } from 'react-i18next'

import { UserContext } from 'components/context/UserContext'
import { LocalKey } from 'constants/LocalKey'

export const LogoutScreen: React.FC = () => {
  const { t } = useTranslation('auth')
  const { setToken } = useContext(UserContext)

  useEffect(() => {
    AsyncStorage.setItem(LocalKey.TOKEN, '').then(() => setToken(null))
  }, [setToken])

  return <Text>{t('logout.header')}</Text>
}

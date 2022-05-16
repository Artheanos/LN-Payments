import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useEffect, useState } from 'react'
import { Box, Button, Center, Flex, Heading, Spinner } from 'native-base'
import { Field, Formik, FormikHelpers } from 'formik'
import { useTranslation } from 'react-i18next'

import { FormikInput } from 'components/form/FormikInput'
import { LocalKey } from 'constants/LocalKey'
import { Role } from 'common-ts/dist/webServiceApi/interface/user'
import { UserContext } from 'components/context/UserContext'
import { api } from 'api'
import { initialValuesFactory, LoginFormData } from './loginForm'
import { isValidUrl, toValidUrl } from 'utils/strings'

export const LoginScreen: React.FC = () => {
  const { setToken } = useContext(UserContext)
  const [loading, setLoading] = useState(false)
  const [initialValues, setInitialValues] = useState<LoginFormData | null>(null)
  const { t } = useTranslation('auth')

  const errors: Record<string, [string, string]> = {
    credentials: ['email', t('login.errors.invalidCredentials')],
    unauthorized: ['email', t('login.errors.adminsOnly')],
    network: ['url', t('login.errors.networkError')],
    invalidUrl: ['url', t('login.errors.invalidUrl')],
  }

  useEffect(() => {
    initialValuesFactory().then((data) => setInitialValues(data))
  }, [setInitialValues])

  const onSuccess = async (token: string, url: string) => {
    await AsyncStorage.multiSet([
      [LocalKey.TOKEN, token],
      [LocalKey.HOST_URL, toValidUrl(url)],
    ])
    setToken(token)
  }

  const onSubmit = async (
    values: LoginFormData,
    helpers: FormikHelpers<LoginFormData>,
  ) => {
    setLoading(true)
    if (!isValidUrl(values.url)) {
      setLoading(false)
      helpers.setFieldError(...errors.invalidUrl)
      return
    }

    const url = toValidUrl(values.url)
    api.auth
      .tryLogin(values, url, 3000)
      .then(({ data, status }) => {
        if (data?.role === Role.ADMIN) return onSuccess(data.token, url)

        let error: keyof typeof errors = 'network'

        if (data) error = 'unauthorized'
        else if (status === 401 || status === 400) error = 'credentials'

        helpers.setFieldError(...errors[error])
      })
      .finally(() => setLoading(false))
  }

  if (!initialValues) return <Spinner />

  return (
    <Center justifyContent="center" h="100%">
      <Formik initialValues={initialValues} onSubmit={onSubmit}>
        {({ handleSubmit }) => (
          <Flex py="10" w="75%" maxWidth="300px">
            <Heading>{t('login.header')}</Heading>
            <Box h={10} />
            <Field
              name="email"
              placeholder={t('login.email')}
              component={FormikInput}
              autoCapitalize="none"
              keyboardType="email-address"
            />
            <Box h={10} />
            <Field
              name="password"
              placeholder={t('login.password')}
              component={FormikInput}
              type="password"
            />
            <Box h={10} />
            <Field
              name="url"
              placeholder={t('login.url')}
              component={FormikInput}
              autoCapitalize="none"
            />
            <Box h={10} />
            <Button isLoading={loading} onPress={() => handleSubmit()}>
              {t('login.submit')}
            </Button>
          </Flex>
        )}
      </Formik>
    </Center>
  )
}

import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useState } from 'react'
import { Box, Button, Center, Flex, Heading } from 'native-base'
import { Field, Formik, FormikHelpers } from 'formik'

import { FormikInput } from 'components/form/FormikInput'
import { LocalKey } from 'constants/LocalKey'
import { LoginForm, LoginResponse } from 'webService/interface/auth'
import { Role } from 'webService/interface/user'
import { UserContext } from 'components/context/UserContext'
import { api, requests } from 'webService/requests'
import { initialValues } from './loginForm'
import { isValidUrl } from 'utils/strings'
import R from 'res/R'

/**
 * Component displaying login screen.
 */
export const LoginScreen: React.FC = () => {
  const { user, updateUser } = useContext(UserContext)
  const [loading, setLoading] = useState(false)

  /**
   * Callback invoked on api call success.
   *
   * @param email  User email
   * @param hostUrl  Backend URL
   * @param token  JWT token required to authorize.
   * @param notificationChannelId  Topic id what is a source of push notifications.
   */
  const onSuccess = async (
    { email, hostUrl }: LoginForm,
    { token, notificationChannelId }: LoginResponse,
  ) => {
    requests.setToken(token)
    requests.host = hostUrl
    await AsyncStorage.multiSet([
      [LocalKey.TOKEN, token],
      [LocalKey.EMAIL, email],
      [LocalKey.HOST_URL, hostUrl],
      [LocalKey.NOTIFICATION_CHANNEL, notificationChannelId],
    ])
    updateUser({ email, token, hostUrl, notificationChannelId })
  }

  /**
   * Callback invoked on request failure.
   *
   * @param setFieldError  Method that sets field errors.
   */
  const onFailure = ({ setFieldError }: FormikHelpers<LoginForm>) => {
    setFieldError('email', R.strings.login.invalidCredentials)
  }

  /**
   * Callback invoked when user is unauthorized.
   *
   * @param setFieldError  Method that sets field errors.
   */
  const onUnauthorized = ({ setFieldError }: FormikHelpers<LoginForm>) => {
    setFieldError('email', R.strings.login.unauthorized)
  }

  /**
   * Invoked when login from is submitted. Handles the backend part of the logging process.
   *
   * @param formValues  Form data
   * @param helpers  Set of helper methods for formik forms.
   */
  const onSubmit = async (
    formValues: LoginForm,
    helpers: FormikHelpers<LoginForm>,
  ) => {
    setLoading(true)

    if (!isValidUrl(formValues.hostUrl)) {
      setLoading(false)
      helpers.setFieldError('hostUrl', R.strings.login.invalidUrl)
      return
    }

    api.auth
      .tryLogin(formValues)
      .then(({ data }) => {
        if (data?.role === Role.ADMIN) return onSuccess(formValues, data)

        if (!data) return onFailure(helpers)

        onUnauthorized(helpers)
      })
      .catch(() => {
        alert(R.strings.login.error)
      })
      .finally(() => setLoading(false))
  }

  return (
    <Center justifyContent="center" h="100%">
      <Formik initialValues={initialValues(user.hostUrl)} onSubmit={onSubmit}>
        {({ handleSubmit }) => (
          <Flex py="10" w="75%" maxWidth="300px">
            <Heading>{R.strings.login.header}</Heading>
            <Box h={10} />
            <Field
              name="email"
              component={FormikInput}
              autoCapitalize="none"
              keyboardType="email-address"
            />
            <Box h={10} />
            <Field name="password" component={FormikInput} type="password" />
            <Box h={10} />
            <Field
              name="hostUrl"
              component={FormikInput}
              autoCapitalize="none"
            />
            <Box h={10} />
            <Button isLoading={loading} onPress={() => handleSubmit()}>
              {R.strings.login.submit}
            </Button>
          </Flex>
        )}
      </Formik>
    </Center>
  )
}

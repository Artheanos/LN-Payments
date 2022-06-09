import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useState } from 'react'
import { Box, Button, Center, Flex, Heading } from 'native-base'
import { Field, Formik, FormikHelpers } from 'formik'

import { FormikInput } from 'components/form/FormikInput'
import { LocalKey } from 'constants/LocalKey'
import { LoginForm } from 'webService/interface/auth'
import { Role } from 'webService/interface/user'
import { UserContext } from 'components/context/UserContext'
import { api } from 'webService/requests'
import { initialValues } from './loginForm'
import { isValidUrl } from 'utils/strings'

export const LoginScreen: React.FC = () => {
  const { user, updateUser } = useContext(UserContext)
  const [loading, setLoading] = useState(false)

  const onSuccess = async ({ email, hostUrl }: LoginForm, token: string) => {
    await AsyncStorage.multiSet([
      [LocalKey.TOKEN, token],
      [LocalKey.EMAIL, email],
      [LocalKey.HOST_URL, hostUrl],
    ])
    updateUser({ email, token, hostUrl })
  }

  const onFailure = ({ setFieldError }: FormikHelpers<LoginForm>) => {
    setFieldError('email', 'Invalid credentials')
  }

  const onUnauthorized = ({ setFieldError }: FormikHelpers<LoginForm>) => {
    setFieldError('email', 'This app is for admins only')
  }

  const onSubmit = async (
    formValues: LoginForm,
    helpers: FormikHelpers<LoginForm>,
  ) => {
    setLoading(true)
    if (!isValidUrl(formValues.hostUrl)) {
      setLoading(false)
      helpers.setFieldError('hostUrl', 'Invalid url')
      return
    }
    api.auth
      .tryLogin(formValues)
      .then(({ data }) => {
        console.log(data)
        if (data?.role === Role.ADMIN) return onSuccess(formValues, data.token)

        if (!data) return onFailure(helpers)

        onUnauthorized(helpers)
      })
      .finally(() => setLoading(false))
  }

  return (
    <Center justifyContent="center" h="100%">
      <Formik initialValues={initialValues(user.hostUrl)} onSubmit={onSubmit}>
        {({ handleSubmit }) => (
          <Flex py="10" w="75%" maxWidth="300px">
            <Heading>Login</Heading>
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
              Login
            </Button>
          </Flex>
        )}
      </Formik>
    </Center>
  )
}

import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useState } from 'react'
import { Box, Button, Center, Flex, Heading } from 'native-base'
import { Field, Formik, FormikHelpers } from 'formik'

import { api } from 'api'
import { initialValues } from './loginForm'
import { FormikInput } from 'components/form/FormikInput'
import { LoginForm } from 'common-ts/dist/webServiceApi/interface/auth'
import { UserContext } from 'components/context/UserContext'

export const LoginScreen: React.FC = () => {
  const { updateUser } = useContext(UserContext)
  const [loading, setLoading] = useState(false)

  const onSuccess = async (email: string, token: string) => {
    await AsyncStorage.multiSet([
      ['token', token],
      ['email', email],
    ])
    updateUser({ email, token })
  }

  const onFailure = ({ setFieldError }: FormikHelpers<LoginForm>) => {
    setFieldError('email', 'Invalid credentials')
  }

  const onUnauthorized = ({ setFieldError }: FormikHelpers<LoginForm>) => {
    setFieldError('email', 'This app is for admins only')
  }

  const onSubmit = async (
    values: LoginForm,
    helpers: FormikHelpers<LoginForm>,
  ) => {
    setLoading(true)
    api.auth.login(values).then(({ data }) => {
      setLoading(false)
      if (data?.role === 'ROLE_ADMIN')
        return onSuccess(values.email, data.token)

      if (!data) return onFailure(helpers)

      onUnauthorized(helpers)
    })
  }

  return (
    <Center justifyContent="center" h="100%">
      <Formik initialValues={initialValues} onSubmit={onSubmit}>
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
            <Button isLoading={loading} onPress={() => handleSubmit()}>
              Login
            </Button>
          </Flex>
        )}
      </Formik>
    </Center>
  )
}

import AsyncStorage from '@react-native-async-storage/async-storage'
import React, { useContext, useState } from 'react'
import axios from 'axios'
import { Box, Button, Center, Flex, Heading, Text } from 'native-base'
import { Field, Formik, FormikHelpers, FormikValues } from 'formik'

import { FormikInput } from '../../../form/FormikInput'
import { UserContext } from '../../../context/UserContext'
import { initialValues } from './loginForm'

const loginUrl = 'http://192.168.8.112:8080/api/auth/login'

export const LoginScreen: React.FC = () => {
  const { setToken } = useContext(UserContext)
  const [loading, setLoading] = useState(false)

  const onSuccess = async (token: string) => {
    await AsyncStorage.setItem('token', token)
    setToken(token)
  }

  const onSubmit = async (
    values: FormikValues,
    helpers: FormikHelpers<typeof initialValues>,
  ) => {
    setLoading(true)

    axios
      .post(loginUrl, values)
      .then(({ data }) => {
        if (data.role === 'ROLE_ADMIN') {
          onSuccess(data.token)
        } else {
          helpers.setFieldError('email', 'This app is for admins only')
        }
      })
      .catch(() => {
        helpers.setFieldError('email', 'Invalid credentials')
      })
      .finally(() => setLoading(false))
  }

  return (
    <Center justifyContent="center" h="100%">
      <Text>Login</Text>
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

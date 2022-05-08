import React, { useContext } from 'react'
import { Box, Button, Center, Flex, Heading, Input } from 'native-base'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { UserContext } from '../../components/context/UserContext'

export const LoginPage: React.FC = () => {
  const { setToken } = useContext(UserContext)

  const submit = async () => {
    const token = '123'
    await AsyncStorage.setItem('token', token)
    setToken(token)
  }

  return (
    <Center justifyContent="center" h="100%">
      <Flex py="10" w="75%" maxWidth="300px">
        <Heading>Login</Heading>
        <Box h={10} />
        <Input
          placeholder="Email"
          autoCapitalize="none"
          keyboardType="email-address"
        />
        <Box h={10} />
        <Input placeholder="Password" type="password" />
        <Box h={10} />
        <Button onPress={submit}>Login</Button>
      </Flex>
    </Center>
  )
}

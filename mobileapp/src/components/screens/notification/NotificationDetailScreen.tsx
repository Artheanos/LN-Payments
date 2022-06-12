import React from 'react'
import { Center, Spinner, Text } from 'native-base'
import { StackScreenProps } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'

export const NotificationDetailScreen: React.FC<
  StackScreenProps<SignInRouterProps, 'Notification details'>
> = ({
  route: {
    params: { id, type, message, address, amount, status },
  },
}) => {
  return (
    <Center justifyContent="center" h="100%">
      <Text>{id}</Text>
    </Center>
  )
}

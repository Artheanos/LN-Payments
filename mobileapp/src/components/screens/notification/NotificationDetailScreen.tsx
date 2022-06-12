import React from 'react'
import { Center, Text } from 'native-base'
import { StackScreenProps } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import R from 'res/R'

/**
 * Displays details of the clicked notification.
 */
export const NotificationDetailScreen: React.FC<
  StackScreenProps<SignInRouterProps, typeof R.routes.notificationDetails>
> = ({
  route: {
    params: { notificationId },
  },
}) => {
  return (
    <Center justifyContent="center" h="100%">
      <Text>{notificationId}</Text>
    </Center>
  )
}

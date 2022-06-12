import React, { useCallback, useEffect, useState } from 'react'
import { Box, Divider, HStack, Pressable, Spinner, Text } from 'native-base'
import { NotificationDetails } from 'webService/interface/notification'
import { StackNavigationProp } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import Icon from 'react-native-vector-icons/MaterialIcons'

type Props = {
  navigation: StackNavigationProp<SignInRouterProps>
  item: NotificationDetails
}

export const NotificationsListEntry: React.FC<Props> = ({
  navigation,
  item,
}) => {
  return (
    <Pressable
      onPress={() => navigation.navigate('Notification details', item)}
    >
      <HStack space={3} alignItems="center" py={5} px={2}>
        <Icon name="check-circle-outline" size={75}></Icon>
        <Box>
          <Text bold fontSize="20">
            {item.message}
          </Text>
          <Text fontSize="15">{item.type}</Text>
        </Box>
      </HStack>
      <Divider />
    </Pressable>
  )
}

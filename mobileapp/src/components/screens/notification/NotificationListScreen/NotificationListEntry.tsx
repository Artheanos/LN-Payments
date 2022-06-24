import React from 'react'
import { Box, Divider, HStack, Pressable, Text } from 'native-base'
import { NotificationDetails } from 'webService/interface/notification'
import { StackNavigationProp } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import { NotificationIcon } from 'components/screens/notification/NotificationListScreen/NotificationIcon'
import R from 'res/R'

type Props = {
  navigation: StackNavigationProp<SignInRouterProps>
  item: NotificationDetails
}

/**
 * Single notification component displayed in notifications list. Should not be used anywhere else. Displays
 * status icon, message and type. On user's click redirects to notification details page.
 *
 * @param navigation  Navigation prop for sign in stack
 * @param item  Single notification object
 */
export const NotificationsListEntry: React.FC<Props> = ({
  navigation,
  item,
}) => {
  return (
    <Pressable
      onPress={() =>
        navigation.navigate(R.routes.notificationDetails, {
          notificationId: item.id,
        })
      }
    >
      <HStack space={3} alignItems="center" py={5} px={2}>
        <NotificationIcon status={item.status} />
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

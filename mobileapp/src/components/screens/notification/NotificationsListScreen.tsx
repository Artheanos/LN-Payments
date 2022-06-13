import React, { useCallback, useEffect, useState } from 'react'
import { Box, Divider, Spinner, Text } from 'native-base'
import { NotificationDetails } from 'webService/interface/notification'
import { api } from 'webService/requests'
import { FlatList } from 'react-native'
import { StackNavigationProp } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import { NotificationsListEntry } from 'components/screens/notification/NotificationListEntry'

type Props = {
  navigation: StackNavigationProp<SignInRouterProps>
}

export const NotificationsListScreen: React.FC<Props> = ({ navigation }) => {
  const [loading, setLoading] = useState(false)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [pageNumber, setPageNumber] = useState(0)
  const [hasMore, setHasMore] = useState(false)
  const [notifications, setNotifications] = useState<NotificationDetails[]>([])

  const queryElements = useCallback(async (page = 0, size = 10) => {
    setPageNumber(page)
    const { data } = await api.notifications.getUserNotifications({
      page,
      size,
    })
    if (data) {
      setHasMore(!data.last)
      setNotifications((notifications) => [...notifications, ...data.content])
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    setLoading(true)
    setNotifications([])
    queryElements()
  }, [queryElements])

  const loadElements = () => {
    if (hasMore) {
      queryElements(pageNumber + 1)
    }
  }

  const onRefresh = async () => {
    setIsRefreshing(true)
    setLoading(true)
    setNotifications([])
    await queryElements()
    setIsRefreshing(false)
  }

  const CenterText: React.FC = ({ children }) => {
    return (
      <Box flex={1} alignItems="center" justifyContent="center">
        {children}
      </Box>
    )
  }

  return (
    <Box flex={1}>
      <Box height={16} justifyContent={'center'} px={2}>
        <Text fontSize={28} fontWeight={'600'} color={'primary.500'}>
          Notifications
        </Text>
      </Box>
      <Divider thickness={2} />
      {loading ? (
        <CenterText>
          <Spinner color="primary.500" size="lg" />
        </CenterText>
      ) : (
        <Box px={2} flex={1}>
          <FlatList
            onEndReached={loadElements}
            ListEmptyComponent={
              <CenterText>
                <Text italic fontSize={15} color={'gray.500'}>
                  Nothing there yet
                </Text>
              </CenterText>
            }
            data={notifications}
            onRefresh={onRefresh}
            refreshing={isRefreshing}
            keyExtractor={(item) => item.id}
            renderItem={({ item }) => (
              <NotificationsListEntry navigation={navigation} item={item} />
            )}
          />
        </Box>
      )}
    </Box>
  )
}

import React, { useCallback, useContext, useEffect, useState } from 'react'
import { Box, Divider, Spinner, Text } from 'native-base'
import { NotificationDetails } from 'webService/interface/notification'
import { api } from 'webService/requests'
import { Alert, FlatList } from 'react-native'
import { StackNavigationProp } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import { NotificationsListEntry } from 'components/screens/notification/NotificationListScreen/NotificationListEntry'
import R from 'res/R'
import { useIsFocused } from '@react-navigation/native'
import { UserContext } from 'components/context/UserContext'

type Props = {
  navigation: StackNavigationProp<SignInRouterProps>
}

/**
 * Renders a list of notifications fetched from the backend.
 *
 * @param navigation  Navigation prop for sign in stack
 */
export const NotificationListScreen: React.FC<Props> = ({ navigation }) => {
  const [loading, setLoading] = useState(false)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [pageNumber, setPageNumber] = useState(0)
  const [hasMore, setHasMore] = useState(false)
  const [notifications, setNotifications] = useState<NotificationDetails[]>([])
  const { logoutUser } = useContext(UserContext)
  const isFocused = useIsFocused()

  /**
   * Responsible for calling backend to obtain credentials. It calls the backend and extracts the content
   * from pageable object.
   *
   * @param page  Page number that you want to request. Default value is 0.
   * @param size  Page size that will be requested. Default value is 10.
   */
  const queryElements = useCallback(
    async (page = 0, size = 10) => {
      setPageNumber(page)
      api.notifications
        .getUserNotifications({
          page,
          size,
        })
        .then(({ data, status }) => {
          if (status === 401) {
            logoutUser()
            Alert.alert(R.strings.logout.timeout)
          }
          if (data) {
            setHasMore(!data.last)
            setNotifications((notifications) => [
              ...notifications,
              ...data.content,
            ])
          }
          setLoading(false)
        })
        .catch(() => {
          logoutUser()
        })
    },
    [logoutUser],
  )

  /**
   * Effect responsible for loading notification data. Invoked whenever queryElements is invoked, or screen is back
   * in focus
   */
  useEffect(() => {
    setLoading(true)
    setNotifications([])
    queryElements()
  }, [queryElements, isFocused])

  /**
   * Loads additional data, when user scrolls to the bottom of the page. Doesn't do anything when
   * no more pages are available.
   */
  const loadElements = () => {
    if (hasMore) {
      queryElements(pageNumber + 1)
    }
  }

  /**
   * Invoked by {@link FlatList} when tries to refresh the list (gesture of pulling the list up). Sets correct
   * flags, reset content and call backend the same way as on the page load.
   */
  const onRefresh = async () => {
    setIsRefreshing(true)
    setLoading(true)
    setNotifications([])
    await queryElements()
    setIsRefreshing(false)
  }

  /**
   * Small component used to center elements within the screen. In order to use it every parent component must
   * have flex parameter set to 1.
   */
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
          {R.strings.notifications.header}
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
                  {R.strings.notifications.emptyList}
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

import React, { useCallback, useContext, useEffect, useState } from 'react'
import { Box, Heading, List, Text } from 'native-base'
import { UserContext } from 'components/context/UserContext'
import { Pageable } from 'webService/interface/pageable'
import { NotificationDetails } from 'webService/interface/notification'
import { api } from 'webService/requests'
import { FlatList } from 'react-native'

export const MainScreen: React.FC = () => {
  const { user } = useContext(UserContext)
  const [loading, setLoading] = useState(true)

  const [notifications, setNotifications] =
    useState<Pageable<NotificationDetails>>()

  const queryElements = useCallback(async (page = 0, size = 10) => {
    setLoading(true)
    const { data } = await api.notifications.getUserNotifications({
      page,
      size,
    })
    console.log('mielimyyy')
    if (data) {
      setNotifications(data)
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    queryElements()
  }, [queryElements])
  return (
    <Box>
      <Heading>{JSON.stringify(user.publicKey)}</Heading>
      <FlatList
        data={notifications?.content}
        renderItem={({ item }) => (
          <Box>
            <Text>{item.id}</Text>
          </Box>
        )}
      />
    </Box>
  )
}

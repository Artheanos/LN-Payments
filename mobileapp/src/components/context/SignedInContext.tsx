import React, { createContext, useContext, useEffect, useState } from 'react'
import { Client as StompClient } from '@stomp/stompjs'
import { requests } from 'webService/requests'
import { routes } from 'webService/routes'
import { NotificationDetails } from 'webService/interface/notification'
import { UserContext } from 'components/context/UserContext'
import { Notification, Notifications } from 'react-native-notifications'
import R from 'res/R'

interface SignedInContextValue {
  socket: StompClient | null
  setSocket: (client: StompClient) => void
}

export const SignedInContext = createContext<SignedInContextValue>({
  socket: null,
  setSocket: () => {},
})

export const SignedInContextProvider: React.FC = ({ children }) => {
  const { user } = useContext(UserContext)
  const [socket, setSocket] = useState<StompClient | null>(null)

  useEffect(() => {
    if (!user.notificationChannelId) return

    const websocket = websocketBuilder(requests.authHeader!, () => {
      setSocket(websocket)
      websocket.subscribe(`/topic/${user.notificationChannelId}`, (message) => {
        showNotification(JSON.parse(message.body) as NotificationDetails)
      })
    })
    websocket.activate()
    return () => {
      websocket.deactivate()
    }
  }, [setSocket, user.notificationChannelId])

  return (
    <SignedInContext.Provider value={{ socket, setSocket }}>
      {children}
    </SignedInContext.Provider>
  )
}

const websocketBuilder = (authorization: string, onConnect: () => void) => {
  return new StompClient({
    appendMissingNULLonIncoming: true,
    forceBinaryWSFrames: true,
    brokerURL: routes.transactions.ws(requests.host!),
    connectHeaders: {
      Authorization: authorization,
    },
    onConnect,
    debug: console.debug,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  })
}

const showNotification = (notification: NotificationDetails) => {
  Notifications.postLocalNotification({
    body: `${notification.amount} ${R.strings.common.currency.satoshi}`,
    title: notification.message,
    identifier: notification.id,
  } as Notification)
}

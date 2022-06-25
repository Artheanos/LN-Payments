import React, { createContext, useContext, useEffect, useState } from 'react'
import { Client as StompClient } from '@stomp/stompjs'
import { requests } from 'webService/requests'
import { routes } from 'webService/routes'
import { NotificationDetails } from 'webService/interface/notification'
import { UserContext } from 'components/context/UserContext'
import R from 'res/R'
import notifee from '@notifee/react-native'

/**
 * Interface defining types for sign in context.
 */
interface SignedInContextValue {
  socket: StompClient | null
  setSocket: (client: StompClient) => void
}

/**
 * Context used in the application for websocket handling. In the future in might also include other features of
 * the signed-in user.
 */
export const SignedInContext = createContext<SignedInContextValue>({
  socket: null,
  setSocket: () => {},
})

/**
 * Provides a sign in context.
 */
export const SignedInContextProvider: React.FC = ({ children }) => {
  const { user } = useContext(UserContext)
  const [socket, setSocket] = useState<StompClient | null>(null)

  /**
   * Establishes the connection by websocket protocol and subscribes to the topic of the user. If channel id
   * is not present, connection is not established. Method is automatically invoked when client is modified, or
   * when user channel id is changed.
   */
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

/**
 * Build a new websocket client, that is used to receive notifications from the backend.
 *
 * @param authorization  JWT token required to authorize with API
 * @param onConnect  Method invoked when connection is established.
 */
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

/**
 * Sends a local push notification for the notification details returned from the API.
 *
 * @param notification  Notification details returned from the api.
 */
const showNotification = async (notification: NotificationDetails) => {
  const channelId = await notifee.createChannel(R.notifications)

  await notifee.displayNotification({
    body: `${notification.amount} ${R.strings.common.currency.satoshi}`,
    title: notification.message,
    id: notification.id,
    android: { channelId },
  })
}

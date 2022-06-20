import React, { useContext, useState } from 'react'
import { Button, Center, Text, useToast } from 'native-base'
import { StackScreenProps } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import R from 'res/R'
import { api } from 'webService/requests'
import { UserContext } from 'components/context/UserContext'
import { signTx } from 'utils/bitcoin'
import { toHexString } from 'utils/hex'

/**
 * Displays details of the clicked notification.
 */
export const NotificationDetailScreen: React.FC<
  StackScreenProps<SignInRouterProps, typeof R.routes.notificationDetails>
> = ({
  route: {
    params: { id, type, message, address, amount, status },
  },
}) => {
  const [processing, setProcessing] = useState(false)
  const { user } = useContext(UserContext)
  const toast = useToast()

  const confirm = () => {
    console.log(toHexString(Buffer.from(user.privateKey!)))
    api.notifications
      .getConfirmationDetails(id)
      .then(async ({ data }) => {
        if (data) {
          const signedTx = signTx(
            data.rawTransaction,
            Buffer.from(user.privateKey!),
            data.redeemScript!,
          )
          console.log(signedTx)
          const { status } = await api.notifications.confirmNotification(id, {
            rawTransaction: signedTx,
            version: data.version,
          })
          if (status === 200) {
            toast.show({
              description: 'Confirmed successfully!',
            })
          } else {
            toast.show({
              description: 'Error while confirming transaction!',
            })
          }
        } else {
          toast.show({
            description: 'Error while confirming transaction!',
          })
        }
      })
      .catch(() => {
        toast.show({
          description: 'Error while confirming transaction!',
        })
      })
  }

  const deny = async () => {
    const { status } = await api.notifications.denyNotification(id)
    if (status === 200) {
      toast.show({
        description: 'Denied successfully!',
      })
    } else {
      toast.show({
        description: 'Error while denying transaction!',
      })
    }
  }

  return (
    <Center justifyContent="center" h="100%">
      <Text>{id}</Text>
      <Button onPress={confirm}>confirm</Button>
      <Button onPress={deny}>deny</Button>
    </Center>
  )
}

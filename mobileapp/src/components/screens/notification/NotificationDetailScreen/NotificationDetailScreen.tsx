import React, { useContext, useState, useEffect } from 'react'
import { Button, Center, HStack, Stack, Text } from 'native-base'
import { StackScreenProps } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import R from 'res/R'
import { api } from 'webService/requests'
import { UserContext } from 'components/context/UserContext'
import { signTx } from 'utils/bitcoin'
import { LoadingModal } from 'components/screens/notification/NotificationDetailScreen/LoadingModal'
import { ErrorAlert } from 'components/screens/notification/NotificationDetailScreen/ErrorAlert'
import { Alert } from 'react-native'
import { NotificationDetails } from 'webService/interface/notification'

/**
 * Displays details of the clicked notification.
 */
export const NotificationDetailScreen: React.FC<
  StackScreenProps<SignInRouterProps, typeof R.routes.notificationDetails>
> = ({
  navigation,
  route: {
    params: { notificationId },
  },
}) => {
  const [processing, setProcessing] = useState(false)
  const [errorDialog, setErrorDialog] = useState(false)
  const { user, logoutUser } = useContext(UserContext)
  const [details, setDetails] = useState<NotificationDetails>()

  /**
   * Method to get data from API
   */
  const notificatDetails = async () => {
    const { data } = await api.notifications.getNotificationDetails(
      notificationId,
    )
    setDetails(data)
  }

  /**
   * Hook. Call method to get data from API
   */
  useEffect(() => {
    notificatDetails()
  }, [])
  /**
   * Processes with the confirmation flow. Obtains transaction details, signs the transaction and sends it back.
   * If success, redirect to result page, otherwise show an error modal.
   */
  const confirm = () => {
    setProcessing(true)
    api.notifications
      .getConfirmationDetails(notificationId)
      .then(({ data, status }) => {
        if (status == 401) {
          logoutUser()
          Alert.alert(R.strings.logout.timeout)
        }
        if (data) {
          const signedTx = signTx(
            data.rawTransaction,
            Buffer.from(user.privateKey!),
            data.redeemScript!,
          )
          api.notifications
            .confirmNotification(notificationId, {
              rawTransaction: signedTx,
              version: data.version,
            })
            .then(({ status }) => {
              if (status === 200) {
                setProcessing(false)
                navigation.navigate(R.routes.outcome, { isConfirmation: true })
              }
            })
            .catch(showAlert)
        }
      })
      .catch(() => {
        logoutUser()
      })
  }

  /**
   * Similarly to confirm method, handles denial flow.
   */
  const deny = async () => {
    setProcessing(true)
    const { status } = await api.notifications.denyNotification(notificationId)
    if (status === 200) {
      setProcessing(false)
      navigation.navigate(R.routes.outcome, { isConfirmation: false })
    } else {
      showAlert()
    }
  }

  /**
   * Shows the alert when confirmation/denial failed.
   */
  const showAlert = () => {
    setProcessing(false)
    setErrorDialog(true)
  }

  /**
   * Closes the error alert.
   */
  const alertClose = () => {
    setErrorDialog(false)
  }

  /**
   * Return View with data from NotificationDetails
   */
  return (
    <Center justifyContent="center" h="100%">
      <LoadingModal processing={processing} />
      <ErrorAlert isOpen={errorDialog} close={alertClose} />
      <Text fontSize={16}>{R.strings.details.id}</Text>
      <Text>{details?.id}</Text>
      <Text fontSize={16}>{R.strings.details.type}</Text>
      <Text>{details?.type}</Text>
      <Text fontSize={16}>{R.strings.details.message}</Text>
      <Text>{details?.message}</Text>
      <Text fontSize={16}>{R.strings.details.address}</Text>
      <Text>{details?.address}</Text>
      <Text fontSize={16}>{R.strings.details.amount}</Text>
      <Text>{details?.amount}</Text>
      <Text fontSize={16}>{R.strings.details.status}</Text>
      <Text>{details?.status}</Text>
      <Stack space={10} alignItems="center">
        <HStack space={20} alignItems="center">
          <Button onPress={confirm}>{R.strings.details.btnConfirm}</Button>
          <Button onPress={deny}>{R.strings.details.btnDeny}</Button>
        </HStack>
      </Stack>
    </Center>
  )
}

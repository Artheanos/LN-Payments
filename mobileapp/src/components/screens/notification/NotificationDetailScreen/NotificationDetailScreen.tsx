import React, { useContext, useState, useEffect } from 'react'
import { Box, Button, Center, HStack, Spinner, VStack } from 'native-base'
import { StackScreenProps } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import R from 'res/R'
import { api } from 'webService/requests'
import { UserContext } from 'components/context/UserContext'
import { signTx } from 'utils/bitcoin'
import { ErrorAlert } from 'components/screens/notification/NotificationDetailScreen/ErrorAlert'
import { Alert } from 'react-native'
import { NotificationDetails } from 'webService/interface/notification'
import { NotificationDetailsElement } from './NotificationDetailsElement'
import { LoadingModal } from 'components/screens/notification/NotificationDetailScreen/LoadingModal'

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
  const [loading, setLoading] = useState(true)
  const [errorDialog, setErrorDialog] = useState(false)
  const { user, logoutUser } = useContext(UserContext)
  const [details, setDetails] = useState<NotificationDetails>()

  /**
   * Method to get data from API
   */
  const notificationDetails = async () => {
    try {
      const { data, status } = await api.notifications.getNotificationDetails(
        notificationId,
      )
      if (status === 401) {
        logoutUser()
        Alert.alert(R.strings.logout.timeout)
      }
      setDetails(data)
    } catch (error) {
      logoutUser()
    } finally {
      setLoading(false)
    }
  }

  /**
   * Hook. Call method to get data from API
   */
  useEffect(() => {
    notificationDetails()
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
      .catch(showAlert)
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

  const DetailsScreenButtons = () => {
    return (
      <HStack space={10}>
        <Button size="lg" w="40%" bg="error.600" variant="solid" onPress={deny}>
          {R.strings.details.btnDeny}
        </Button>
        <Button
          size="lg"
          w="40%"
          bg="success.600"
          variant="solid"
          onPress={confirm}
        >
          {R.strings.details.btnConfirm}
        </Button>
      </HStack>
    )
  }

  /**
   * Return View with data from NotificationDetails
   */
  return (
    <Center justifyContent="flex-start" h="100%">
      <LoadingModal processing={processing} />
      <ErrorAlert isOpen={errorDialog} close={alertClose} />
      {loading || !details ? (
        <Box flex={1} alignItems="center" justifyContent="center">
          <Spinner color="primary.500" size="lg" />
        </Box>
      ) : (
        <VStack space={3} marginTop={5}>
          <NotificationDetailsElement
            title={R.strings.details.id}
            data={details.id}
          />
          <NotificationDetailsElement
            title={R.strings.details.type}
            data={details.type}
          />
          <NotificationDetailsElement
            title={R.strings.details.message}
            data={details.message}
          />
          <NotificationDetailsElement
            title={R.strings.details.address}
            data={details.address}
          />
          <NotificationDetailsElement
            title={R.strings.details.amount}
            data={details.amount}
          />
          <NotificationDetailsElement
            title={R.strings.details.status}
            data={details.status}
          />
        </VStack>
      )}
      <Box alignItems="center" position="absolute" bottom="5">
        {details?.status === 'PENDING' && <DetailsScreenButtons />}
      </Box>
    </Center>
  )
}

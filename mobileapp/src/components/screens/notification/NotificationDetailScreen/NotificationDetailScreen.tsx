import React, { useContext, useState, useEffect } from 'react'
import { Box, Button, Center, HStack, Text, VStack } from 'native-base'
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
  const notificationDetails = async () => {
    setProcessing(true)
    try {
      const { data } = await api.notifications.getNotificationDetails(
        notificationId,
      )
      setDetails(data)
    } catch (error) {
      showAlert()
      console.log(error)
    } finally {
      setProcessing(false)
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

  const DetailsNotificationId = () => {
    return (
      <Box alignItems="center">
        <Text fontSize={20} fontWeight="bold">
          {R.strings.details.id}
        </Text>
        <Text italic fontSize={17}>
          {details?.id}
        </Text>
      </Box>
    )
  }

  const DetailsNotificationType = () => {
    return (
      <Box alignItems="center">
        <Text fontSize={20} fontWeight="bold">
          {R.strings.details.type}
        </Text>
        <Text italic fontSize={17}>
          {details?.type}
        </Text>
      </Box>
    )
  }

  const DetailsNotificationMessage = () => {
    return (
      <Box alignItems="center">
        <Text fontSize={20} fontWeight="bold">
          {R.strings.details.message}
        </Text>
        <Text italic fontSize={17}>
          {details?.message}
        </Text>
      </Box>
    )
  }

  const DetailsDestinationAddress = () => {
    return (
      <Box alignItems="center">
        <Text fontSize={20} fontWeight="bold">
          {R.strings.details.address}
        </Text>
        <Text italic fontSize={17}>
          {details?.address}
        </Text>
      </Box>
    )
  }

  const DetailsTransactionAmount = () => {
    return (
      <Box alignItems="center">
        <Text fontSize={20} fontWeight="bold">
          {R.strings.details.amount}
        </Text>
        <Text italic fontSize={17}>
          {details?.amount}
        </Text>
      </Box>
    )
  }

  const DetailsNotificationStatus = () => {
    return (
      <Box alignItems="center">
        <Text fontSize={20} fontWeight="bold">
          {R.strings.details.status}
        </Text>
        <Text italic fontSize={17}>
          {details?.status}
        </Text>
      </Box>
    )
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
      <VStack space={3} marginTop={5}>
        <DetailsNotificationId />
        <DetailsNotificationType />
        <DetailsNotificationMessage />
        <DetailsDestinationAddress />
        <DetailsTransactionAmount />
        <DetailsNotificationStatus />
      </VStack>
      <Box alignItems="center" position="absolute" bottom="5">
        {details?.status === 'PENDING' ? <DetailsScreenButtons /> : null}
      </Box>
    </Center>
  )
}

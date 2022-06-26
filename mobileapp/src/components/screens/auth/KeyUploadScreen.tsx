import axios from 'axios'
import React, { useContext, useEffect, useState } from 'react'
import { Alert } from 'react-native'
import { Box, Center, Heading, Spinner } from 'native-base'
import AsyncStorage from '@react-native-async-storage/async-storage'

import { UserContext } from 'components/context/UserContext'
import { api } from 'webService/requests'
import R from 'res/R'
import { toHexString } from 'utils/hex'
import { generateKeyPair } from 'utils/bitcoin'

/**
 * Screen handling key pair generation and public key upload to the backend.
 */
export const KeyUploadScreen: React.FC = () => {
  const { user, updateUser, logoutUser } = useContext(UserContext)

  const [generated, setGenerated] = useState(false)
  const [uploaded, setUploaded] = useState(false)
  const [saved, setSaved] = useState(false)

  /**
   * Generated public and private key pair, uploads it to the backend and saves the private key in local storage.
   */
  const generateKeys = async () => {
    const { publicKey, privateKey } = generateKeyPair()
    setGenerated(true)

    try {
      const response = await api.admins.uploadKeys({
        publicKey: toHexString(publicKey),
      })

      if (response.status !== 200) {
        if (response.status === 401) {
          logoutUser()
          Alert.alert(R.strings.logout.timeout)
        } else if (response.status === 409) {
          Alert.alert(R.strings.keyUpload.alreadyUploaded)
        } else {
          Alert.alert(R.strings.keyUpload.error)
        }
        updateUser({ token: null, email: null, uploadKeys: false })
        return
      }

      setUploaded(true)

      await AsyncStorage.setItem(
        user.email!,
        JSON.stringify({ publicKey, privateKey }),
      )
      setSaved(true)

      updateUser({ publicKey, privateKey, uploadKeys: false })
    } catch (e) {
      if (axios.isAxiosError(e)) logoutUser()
    }
  }

  /**
   * Invoked on screen load, invokes key generation method.
   */
  useEffect(() => {
    generateKeys()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  /**
   * Determines the messaged displayed under the spinner.
   */
  const message = generated
    ? uploaded
      ? saved
        ? R.strings.keyUpload.redirecting
        : R.strings.keyUpload.saving
      : R.strings.keyUpload.uploading
    : R.strings.keyUpload.generating

  return (
    <Center justifyContent="center" h="100%">
      <Spinner size="lg" />
      <Box h={10} />
      <Heading color="primary.600" fontSize="lg">
        {message}
      </Heading>
    </Center>
  )
}

import React, { useContext, useEffect, useState } from 'react'
import { Box, Center, Heading, Spinner } from 'native-base'

import AsyncStorage from '@react-native-async-storage/async-storage'
import bitcoin from 'bitcoin'
import { UserContext } from 'components/context/UserContext'
import { api } from 'webService/requests'
import R from 'res/R'

export const KeyUploadScreen: React.FC = () => {
  const { user, updateUser } = useContext(UserContext)

  const [generated, setGenerated] = useState(false)
  const [uploaded, setUploaded] = useState(false)
  const [saved, setSaved] = useState(false)

  const generateKeys = async () => {
    const { publicKey, privateKey } = bitcoin.ECPair.makeRandom({
      network: bitcoin.networks.testnet,
    })
    setGenerated(true)

    const response = await api.admins.uploadKeys({
      publicKey: toHexString(publicKey),
    })

    if (response.status !== 200) {
      if (response.status === 409) alert(R.strings.keyUpload.alreadyUploaded)
      else alert(R.strings.keyUpload.error)
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
  }

  useEffect(() => {
    generateKeys()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

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

const toHexString = (byteArray: Buffer) => {
  return Array.from(byteArray, function (byte) {
    return ('0' + (byte & 0xff).toString(16)).slice(-2)
  }).join('')
}

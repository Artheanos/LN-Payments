import React, { useContext, useEffect, useState } from 'react'
import { Box, Center, Heading, Spinner } from 'native-base'

import AsyncStorage from '@react-native-async-storage/async-storage'
import bitcoin from 'bitcoin'
import { NativeStackNavigationProp } from '@react-navigation/native-stack'
import { ParamListBase } from '@react-navigation/native'
import { UserContext } from 'components/context/UserContext'
import { api } from 'api'

interface Props {
  navigation: NativeStackNavigationProp<ParamListBase>
}

export const KeyUploadScreen: React.FC<Props> = ({ navigation }) => {
  const { setPublicKey, setPrivateKey } = useContext(UserContext)

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
      if (response.status === 409) alert('Key already uploaded')
      else alert('Error while uploading keys')
      navigation.navigate('Login')
      return
    }

    setUploaded(true)

    await AsyncStorage.multiSet([
      ['privateKey', toHexString(privateKey)],
      ['publicKey', toHexString(publicKey)],
    ])
    setSaved(true)

    setPublicKey(publicKey)
    setPrivateKey(privateKey)
  }

  useEffect(() => {
    generateKeys()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const message = generated
    ? uploaded
      ? saved
        ? 'Redirecting'
        : 'Saving keys'
      : 'Uploading keys'
    : 'Generating keys'

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

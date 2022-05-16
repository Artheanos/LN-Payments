import { WebServiceApi } from 'common-ts/dist/webServiceApi'
import AsyncStorage from '@react-native-async-storage/async-storage'

import Constants from 'expo-constants'
const { manifest } = Constants

let host = 'ln-payments.com'

const devHost =
  manifest &&
  manifest.packagerOpts?.dev &&
  manifest.debuggerHost?.split(':')?.shift()?.concat(':8080')

if (devHost) host = devHost

const webServiceApi = new WebServiceApi(
  () => AsyncStorage.getItem('token'),
  `http://${host}`,
)

export const { api } = webServiceApi
export const setHost = (host: string) => {
  webServiceApi.host = host
}

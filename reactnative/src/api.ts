import { WebServiceApi } from 'common-ts/dist/webServiceApi'
import AsyncStorage from '@react-native-async-storage/async-storage'

let host = 'ln-payments.com'

const devHost = 'http://192.168.1.165:8080'

if (devHost) host = devHost

export const { api } = new WebServiceApi(
  () => AsyncStorage.getItem('token'),
  host,
)

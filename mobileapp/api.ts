import { WebServiceApi } from 'common-ts/dist/webServiceApi'
import AsyncStorage from '@react-native-async-storage/async-storage'

export const api = new WebServiceApi('http://192.168.8.112:8080', () =>
  AsyncStorage.getItem('token'),
).api

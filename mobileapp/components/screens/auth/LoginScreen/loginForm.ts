import { LoginForm } from 'common-ts/dist/webServiceApi/interface/auth'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { LocalKey } from 'constants/LocalKey'

export const initialValuesFactory = async () => ({
  email: '',
  password: '',
  url: (await AsyncStorage.getItem(LocalKey.HOST_URL)) || 'http://',
})

export interface LoginFormData extends LoginForm {
  url: string
}

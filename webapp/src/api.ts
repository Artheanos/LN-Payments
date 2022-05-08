import { getLocalJson } from 'utils/persist'
import { LocalKey } from 'constants/LocalKey'
import { WebServiceApi } from 'common-ts/dist/webServiceApi'

export type Response<T> = {
  data?: T
  status: number
}

export const refreshTokenFactory = () => getLocalJson(LocalKey.TOKEN)

const webServiceApi = new WebServiceApi(refreshTokenFactory)

export const api = webServiceApi.api

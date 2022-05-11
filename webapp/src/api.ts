import { getLocalJson } from 'utils/persist'
import { LocalKey } from 'constants/LocalKey'
import { WebServiceApi } from 'common-ts/dist/webServiceApi'

export type Response<T> = {
  data?: T
  status: number
}

const webServiceApi = new WebServiceApi(window.location.host, () =>
  getLocalJson(LocalKey.TOKEN)
)

export const getAuthHeader = () => webServiceApi.authHeader()

export const api = webServiceApi.api

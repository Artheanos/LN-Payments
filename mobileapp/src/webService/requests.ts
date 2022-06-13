import axios, { AxiosError, AxiosRequestConfig } from 'axios'
import AsyncStorage from '@react-native-async-storage/async-storage'

import { KeyUploadForm, LoginForm, LoginResponse } from './interface/auth'
import { LocalKey } from 'constants/LocalKey'
import { routes } from './routes'
import { Pageable, PageRequest } from 'webService/interface/pageable'
import { NotificationDetails } from 'webService/interface/notification'

export type Response<T> = {
  data?: T
  status: number
}

const defaultConfig: AxiosRequestConfig = {
  method: 'post',
}

type RefreshTokenFactory = () => Promise<string | null>

class Requests {
  public authHeader: string | null = null
  public host?: string

  private readonly refreshTokenFactory: RefreshTokenFactory

  constructor(refreshTokenFactory: RefreshTokenFactory, host?: string) {
    this.host = host
    this.refreshTokenFactory = refreshTokenFactory
  }

  public api = {
    auth: {
      tryLogin: async (
        { hostUrl, ...data }: LoginForm,
        timeout = 3000,
      ): Promise<Response<LoginResponse>> => {
        console.log(new URL(routes.auth.login, hostUrl).href)
        return this.request(
          new URL(routes.auth.login, hostUrl).href,
          { data, timeout },
          false,
        )
      },
    },
    admins: {
      uploadKeys: (data: KeyUploadForm): Promise<Response<unknown>> =>
        this.request(routes.admins.keys, { method: 'patch', data }),
    },
    notifications: {
      getUserNotifications: async (
        params: PageRequest,
      ): Promise<Response<Pageable<NotificationDetails>>> => {
        return this.request(routes.notifications.index, {
          method: 'get',
          params,
        })
      },
    },
  }

  private async reloadAuthHeader() {
    this.authHeader = `Bearer ${await this.refreshTokenFactory()}`
  }

  private async configFactory(
    url: string,
    config: AxiosRequestConfig,
    authenticate: boolean,
  ) {
    const result = { url, ...defaultConfig, ...config }

    await this.reloadAuthHeader()
    if (this.authHeader && authenticate) {
      if (!result.headers) {
        result.headers = {}
      }
      if (!result.headers.Authorization) {
        result.headers.Authorization = this.authHeader
      }
    }

    return result
  }

  private async request<D, T>(
    url: string,
    config: AxiosRequestConfig<D> = {},
    authenticate = true,
  ): Promise<Response<T>> {
    try {
      url = this.resolveRoute(url)
      const response = await axios.request(
        await this.configFactory(url, config, authenticate),
      )
      return { data: response.data, status: response.status }
    } catch (e) {
      if (axios.isAxiosError(e) && e.response?.status) {
        return { status: e.response.status }
      } else {
        if ((e as AxiosError)?.response?.status === 0) {
          alert('Could not connect to the server')
        }
        throw e
      }
    }
  }

  private resolveRoute(route: string) {
    return this.host && route.startsWith('/') ? `${this.host}${route}` : route
  }
}

export const refreshTokenFactory = async () =>
  await AsyncStorage.getItem(LocalKey.TOKEN)

export const requests = new Requests(refreshTokenFactory)
export const api = requests.api

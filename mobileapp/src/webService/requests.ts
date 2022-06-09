import axios, { AxiosError, AxiosRequestConfig } from 'axios'
import AsyncStorage from '@react-native-async-storage/async-storage'

import { routes } from './routes'
import { KeyUploadForm, LoginForm, LoginResponse } from './interface/auth'

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

  private readonly host?: string
  private readonly refreshTokenFactory: RefreshTokenFactory

  constructor(refreshTokenFactory: RefreshTokenFactory, host?: string) {
    this.host = host
    this.refreshTokenFactory = refreshTokenFactory
  }

  public api = {
    auth: {
      login: async (data: LoginForm): Promise<Response<LoginResponse>> => {
        return await this.request(routes.auth.login, { data })
      },
    },
    admins: {
      uploadKeys: (data: KeyUploadForm): Promise<Response<unknown>> =>
        this.request(routes.admins.keys, { method: 'patch', data }),
    },
  }

  private async reloadAuthHeader() {
    this.authHeader = `Bearer ${await this.refreshTokenFactory()}`
  }

  private async configFactory(url: string, config: AxiosRequestConfig) {
    const result = { url, ...defaultConfig, ...config }

    await this.reloadAuthHeader()
    if (this.authHeader) {
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
  ): Promise<Response<T>> {
    try {
      url = this.resolveRoute(url)
      const response = await axios.request(
        await this.configFactory(url, config),
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
  await AsyncStorage.getItem('token')

export const requests = new Requests(
  refreshTokenFactory,
  'http://192.168.8.112:8080',
)
export const api = requests.api

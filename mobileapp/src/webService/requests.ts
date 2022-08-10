import axios, { AxiosError, AxiosRequestConfig } from 'axios'
import { Alert } from 'react-native'

import {
  ConfirmationDetails,
  NotificationDetails,
} from 'webService/interface/notification'
import { KeyUploadForm, LoginForm, LoginResponse } from './interface/auth'
import { Pageable, PageRequest } from 'webService/interface/pageable'
import { routes } from './routes'
import R from 'res/R'

export type Response<T> = {
  data?: T
  status: number
}

const defaultConfig: AxiosRequestConfig = {
  method: 'post',
}

/**
 * Handles all API calls. Contains all the methods required for request processing and error handling.
 */
class Requests {
  public authHeader: string | null = null
  public host?: string
  public token?: string

  constructor(host?: string) {
    this.host = host
  }

  public setToken(token: string) {
    this.token = token
    this.authHeader = `Bearer ${token}`
  }

  /**
   * Object containing methods for all required api calls. Client can leverage this object for any endpoint call.
   */
  public api = {
    auth: {
      tryLogin: async (
        { hostUrl, ...data }: LoginForm,
        timeout = 3000,
      ): Promise<Response<LoginResponse>> => {
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
      getNotificationDetails: async (
        notificationId: string,
      ): Promise<Response<NotificationDetails>> => {
        return this.request(routes.notifications.details(notificationId), {
          method: 'get',
        })
      },
      getConfirmationDetails: async (
        notificationId: string,
      ): Promise<Response<ConfirmationDetails>> => {
        return this.request(routes.notifications.transaction(notificationId), {
          method: 'get',
        })
      },
      confirmNotification: (
        notificationId: string,
        data: ConfirmationDetails,
      ): Promise<Response<unknown>> =>
        this.request(routes.notifications.confirm(notificationId), {
          data,
        }),
      denyNotification: (notificationId: string): Promise<Response<unknown>> =>
        this.request(routes.notifications.deny(notificationId)),
    },
  }

  /**
   * Created config for the HTTP request
   *
   * @param url Server URL
   * @param config Axios config object
   * @param authenticate boolean flag determining whether user needs authentication for the given endpoint.
   */
  private async configFactory(
    url: string,
    config: AxiosRequestConfig,
    authenticate: boolean,
  ) {
    const result = { url, ...defaultConfig, ...config }

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

  /**
   * Handles single HTTP request.
   *
   * @param url  Server url
   * @param config  Request details like headers and other data.
   * @param authenticate boolean flag determining whether user needs authentication for the given endpoint.
   */
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
          Alert.alert(R.strings.logout.connectionError)
        }
        throw e
      }
    }
  }

  private resolveRoute(route: string) {
    return route.startsWith('/') ? `${this.host!}${route}` : route
  }
}

export const requests = new Requests()
export const api = requests.api

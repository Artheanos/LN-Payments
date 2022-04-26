import axios, { AxiosRequestConfig } from 'axios'

import routesBuilder from 'routesBuilder'
import { datify } from 'utils/time'
import { getLocalJson } from 'utils/persist'
import { LocalKey } from '@constants'
import { WalletForm } from './components/wallet/create/form'

const defaultConfig: AxiosRequestConfig = {
  method: 'post'
}

export const getAuthHeader = () => `Bearer ${getLocalJson(LocalKey.TOKEN)}`

const configFactory = (url: string, config: AxiosRequestConfig) => {
  const result = { url, ...defaultConfig, ...config }
  const authHeader = getAuthHeader()

  if (authHeader) {
    if (!result.headers) {
      result.headers = {}
    }
    if (!result.headers.Authorization) {
      result.headers.Authorization = authHeader
    }
  }

  return result
}

const request = async <D, T>(
  url: string,
  config: AxiosRequestConfig<D> = {}
): Promise<Response<T>> => {
  try {
    const response = await axios.request(configFactory(url, config))
    return { data: response.data, status: response.status }
  } catch (e) {
    if (axios.isAxiosError(e) && e.response?.status) {
      return { status: e.response.status }
    } else {
      throw e
    }
  }
}

export type Response<T> = {
  data?: T
  status: number
}

export const api = {
  auth: {
    login: async (data: LoginForm): Promise<Response<LoginResponse>> => {
      return await request(routesBuilder.api.auth.login, { data })
    },
    register: (data: RegisterForm): Promise<Response<number>> => {
      return request(routesBuilder.api.auth.register, { data })
    },
    refreshToken(): Promise<Response<RefreshTokenResponse>> {
      return request(routesBuilder.api.auth.refreshToken, { method: 'get' })
    }
  },
  payment: {
    create: async (data: PaymentForm): Promise<Response<PaymentDetails>> => {
      const response: Response<PaymentDetails> = await request(
        routesBuilder.api.payments.index,
        { data }
      )

      if (response.data) {
        datify(response.data)
      }

      return response
    },
    info: async (): Promise<Response<PaymentInfo>> => {
      return request(routesBuilder.api.payments.info, { method: 'get' })
    },
    history: async (
      params: PageRequest
    ): Promise<Response<Pageable<PaymentDetails>>> => {
      return request(routesBuilder.api.payments.index, {
        method: 'get',
        params
      })
    },
    historyAll: async (
      params: PageRequest
    ): Promise<Response<Pageable<PublicPaymentDetails>>> => {
      return request(routesBuilder.api.payments.all, {
        method: 'get',
        params
      })
    }
  },
  admins: {
    getAll: async (
      params?: PageRequest
    ): Promise<Response<Pageable<AdminUser>>> =>
      request(routesBuilder.api.admins.index, { method: 'get', params }),

    create: (data: RegisterForm): Promise<Response<unknown>> =>
      request(routesBuilder.api.admins.index, { data })
  },
  wallet: {
    getInfo: (): Promise<Response<WalletInfo>> =>
      request(routesBuilder.api.wallet.index, { method: 'get' }),

    closeChannels: (withForce: boolean): Promise<Response<unknown>> =>
      request(routesBuilder.api.wallet.closeChannels, {
        params: { withForce: withForce }
      }),

    transfer: (): Promise<Response<unknown>> =>
      request(routesBuilder.api.wallet.transfer),

    create: (data: WalletForm): Promise<Response<unknown>> =>
      request(routesBuilder.api.wallet.index, { data })
  }
}

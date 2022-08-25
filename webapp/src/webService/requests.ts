import axios, { AxiosError, AxiosRequestConfig } from 'axios'

import { routes } from './routes'
import { datify } from '../utils/time'
import { Pageable, PageRequest } from './interface/pageable'
import {
  PaymentDetails,
  PaymentForm,
  PaymentInfo,
  PublicPaymentDetails
} from './interface/payment'
import {
  LoginForm,
  LoginResponse,
  RefreshTokenResponse,
  RegisterForm,
  TemporaryForm
} from './interface/auth'
import { WalletForm, WalletInfo } from './interface/wallet'
import { AdminUser, User } from './interface/user'
import {
  NewTransactionInfo,
  TransactionForm,
  TransactionsResponse
} from './interface/transaction'
import { getLocalJson } from '../utils/persist'
import { LocalKey } from 'constants/LocalKey'
import { Settings } from 'webService/interface/settings'
import { PasswordChangeProps } from 'pages/userPanel/account/form'

export type Response<T> = {
  data?: T
  status: number
}

const defaultConfig: AxiosRequestConfig = {
  method: 'post'
}

type RefreshTokenFactory = () => string

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
      register: (data: RegisterForm): Promise<Response<number>> => {
        return this.request(routes.auth.register, { data })
      },
      refreshToken: (
        timeout: number
      ): Promise<Response<RefreshTokenResponse>> => {
        return this.request(routes.auth.refreshToken, {
          method: 'get',
          timeout
        })
      },
      temporary: (
        data: TemporaryForm
      ): Promise<Response<RefreshTokenResponse>> => {
        return this.request(routes.auth.temporary, { data })
      }
    },
    payment: {
      create: async (data: PaymentForm): Promise<Response<PaymentDetails>> => {
        const response: Response<PaymentDetails> = await this.request(
          routes.payments.index,
          { data }
        )

        if (response.data) {
          datify(response.data)
        }

        return response
      },
      info: async (): Promise<Response<PaymentInfo>> => {
        return this.request(routes.payments.info, { method: 'get' })
      },
      history: async (
        params: PageRequest
      ): Promise<Response<Pageable<PaymentDetails>>> => {
        return this.request(routes.payments.index, {
          method: 'get',
          params
        })
      },
      historyAll: async (
        params: PageRequest
      ): Promise<Response<Pageable<PublicPaymentDetails>>> => {
        return this.request(routes.payments.all, {
          method: 'get',
          params
        })
      }
    },
    admins: {
      getAll: async (
        params?: PageRequest
      ): Promise<Response<Pageable<AdminUser>>> =>
        this.request(routes.admins.index, { method: 'get', params }),

      create: (data: RegisterForm): Promise<Response<unknown>> =>
        this.request(routes.admins.index, { data }),

      remove: (data: AdminUser): Promise<Response<unknown>> =>
        this.request(routes.admins.index, { method: 'delete', data })
    },
    wallet: {
      getInfo: (): Promise<Response<WalletInfo>> =>
        this.request(routes.wallet.index, { method: 'get' }),

      closeChannels: (withForce: boolean): Promise<Response<unknown>> =>
        this.request(routes.wallet.closeChannels, {
          params: { withForce: withForce }
        }),

      transfer: (): Promise<Response<unknown>> =>
        this.request(routes.wallet.transfer),

      create: (data: WalletForm): Promise<Response<unknown>> =>
        this.request(routes.wallet.index, { data })
    },
    transactions: {
      getTransactions: async (
        params: PageRequest
      ): Promise<Response<TransactionsResponse>> => {
        return this.request(routes.transactions.index, {
          method: 'get',
          params
        })
      },
      getNewTransactionInfo: async (): Promise<
        Response<NewTransactionInfo>
      > => {
        return this.request(routes.transactions.newInfo, {
          method: 'get'
        })
      },
      createTransaction: (data: TransactionForm): Promise<Response<unknown>> =>
        this.request(routes.transactions.index, { data })
    },
    settings: {
      getSettings: async (): Promise<Response<Settings>> =>
        this.request(routes.settings.index, { method: 'get' }),
      updateSettings: async (data: Settings) =>
        this.request(routes.settings.index, { method: 'put', data })
    },
    users: {
      getUserDetails: async (): Promise<Response<User>> =>
        this.request(routes.users.index, { method: 'get' }),
      changePassword: (
        data: PasswordChangeProps
      ): Promise<Response<number>> => {
        return this.request(routes.users.password, { method: 'put', data })
      }
    }
  }

  private reloadAuthHeader() {
    this.authHeader = `Bearer ${this.refreshTokenFactory()}`
  }

  private configFactory(
    url: string,
    config: AxiosRequestConfig,
    skipAuthentication: boolean
  ) {
    const result = { url, ...defaultConfig, ...config }

    this.reloadAuthHeader()
    if (!skipAuthentication && this.authHeader) {
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
    skipAuthentication = false
  ): Promise<Response<T>> {
    try {
      url = this.resolveRoute(url)
      const response = await axios.request(
        this.configFactory(url, config, skipAuthentication)
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

export const refreshTokenFactory = () => getLocalJson(LocalKey.TOKEN)

export const requests = new Requests(refreshTokenFactory)
export const api = requests.api

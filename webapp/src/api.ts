import axios, { AxiosRequestConfig } from 'axios'

import routesBuilder from 'routesBuilder'
import { datify } from './utils/time'

const request = async <D, T>(
  url: string,
  config: AxiosRequestConfig<D>
): Promise<Response<T>> => {
  try {
    const response = await axios.request({ ...config, url })
    return { data: response.data, status: response.status }
  } catch (e) {
    if (axios.isAxiosError(e)) {
      return { status: e.response!.status }
    } else {
      throw e
    }
  }
}

type Response<T> = {
  data?: T
  status: number
}

export const api = {
  auth: {
    login: async (data: LoginForm): Promise<Response<LoginResponse>> => {
      return await request(routesBuilder.api.auth.login, {
        data,
        method: 'post'
      })
    },
    register: (data: RegisterForm): Promise<Response<number>> => {
      return request(routesBuilder.api.auth.register, { data, method: 'post' })
    }
  },
  payment: {
    create: async (data: PaymentForm): Promise<Response<PaymentDetails>> => {
      const response: Response<PaymentDetails> = await request(
        routesBuilder.api.payments.index,
        { data, method: 'post' }
      )

      if (response.data) {
        datify(response.data)
      }

      return response
    }
  }
}

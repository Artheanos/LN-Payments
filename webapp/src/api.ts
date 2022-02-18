import axios from 'axios'

import routesBuilder from './routesBuilder'
import { datify } from './utils/time'

export const api = {
  payment: {
    create: async (body: {
      email: string
      numberOfTokens: number
    }): Promise<PaymentDetails> => {
      const response = await axios.post(routesBuilder.api.payments.index, body)
      return datify(response.data)
    }
  }
}

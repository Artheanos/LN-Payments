export enum PaymentStatus {
  PENDING = 'PENDING',
  CANCELLED = 'CANCELLED',
  COMPLETE = 'COMPLETE'
}

export interface PaymentForm {
  numberOfTokens: number
}

export interface PublicPaymentDetails {
  email: string
  numberOfTokens: number
  paymentStatus: PaymentStatus
  price: number
  timestamp: Date
}

export interface PaymentDetails {
  expirationTimestamp: Date
  paymentRequest: string
  paymentTopic: string
  timestamp: Date
  price: number
  numberOfTokens: number
  paymentStatus: PaymentStatus
  tokens: string[]
}

export interface PaymentInfo {
  price: number
  description: string
  nodeUrl: string
  pendingPayments: PaymentDetails[]
}

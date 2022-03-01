interface PaymentDetails {
  paymentRequest: string
  timestamp: Date
  expirationTimestamp: Date
}

interface PaymentInfo {
  price: number
  description: string
  nodeUrl: string
  pendingPayments: PaymentDetails[]
}

interface RegisterRequest {
  email: string
  fullName: string
  password: string
}

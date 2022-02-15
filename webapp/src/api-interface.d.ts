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

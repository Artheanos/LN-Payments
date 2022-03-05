interface PaymentForm {
  email: string
  numberOfTokens: number
}

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

interface RegisterForm {
  email: string
  fullName: string
  password: string
}

interface LoginForm {
  email: string
  password: string
}

interface LoginResponse {
  email: string
}

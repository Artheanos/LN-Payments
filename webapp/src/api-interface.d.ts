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

interface RefreshTokenResponse {
  token: string
}

enum Role {
  USER,
  ADMIN,
  TEMPORARY
}

interface RegisterForm {
  email: string
  fullName: string
  password: string
}

interface User {
  email: string
  fullName: string
  role: Role
}

interface LoginForm {
  email: string
  password: string
}

interface LoginResponse extends User {
  token: string
}

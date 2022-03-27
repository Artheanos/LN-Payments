interface PaymentForm {
  numberOfTokens: number
}

interface TempUserRequest {
  email: string
}

interface TempUserResponse {
  emailId: string
  token: string
}

interface PaymentDetails {
  expirationTimestamp: Date
  paymentRequest: string
  paymentTopic: string
  timestamp: Date
}

interface WsTransactionResponse {
  tokens: string[]
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

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
  price: number
  numberOfTokens: number
  paymentStatus: PaymentStatus
  tokens: string[]
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

interface RegisterForm {
  email: string
  fullName: string
  password: string
}

interface User {
  email: string
  fullName: string
  role: string
}

interface LoginForm {
  email: string
  password: string
}

interface LoginResponse extends User {
  token: string
}

interface Pageable<T> {
  content: T[]
  empty: boolean
  first: boolean
  last: boolean
  number: number
  numberOfElements: number
  pageable: PageInfo
  size: number
  sort: Sort
  totalElements: number
  totalPages: number
}

interface Sort {
  empty: boolean
  sorted: boolean
  unsorted: boolean
}

interface PageInfo {
  offset: number
  pageNumber: number
  pageSize: number
  paged: boolean
  sort: Sort
  unpaged: boolean
}

interface PageRequest {
  page: number
  size: number
}

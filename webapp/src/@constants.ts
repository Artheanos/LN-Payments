export enum LocalKey {
  TOKEN = 'token',
  USER = 'user',
  PAYMENT = 'paymentDetails',
  TRANSACTION_TOKENS = 'transactionTokens'
}

export enum PaymentStatus {
  PENDING,
  CANCELED,
  COMPLETE
}

export enum Role {
  USER = 'ROLE_USER',
  ADMIN = 'ROLE_ADMIN',
  TEMPORARY = 'ROLE_TEMPORARY'
}

export interface NotificationDetails {
  id: string
  message: string
  status: NotificationStatus
  type: NotificationType
  amount: number
  address: string
}

export enum NotificationStatus {
  CONFIRMED = 'CONFIRMED',
  DENIED = 'DENIED',
  PENDING = 'PENDING',
  EXPIRED = 'EXPIRED',
}

export enum NotificationType {
  TRANSACTION = 'TRANSACTION',
  WALLET_RECREATION = 'WALLET_RECREATION',
}

export interface ConfirmationDetails {
  rawTransaction: string
  version: number
  redeemScript?: string
}

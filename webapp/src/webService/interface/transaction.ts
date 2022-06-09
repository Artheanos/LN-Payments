import { Pageable } from './pageable'
import { BitcoinWalletBalance } from './wallet'

export interface WsTransactionResponse {
  tokens: string[]
}

export interface TransactionsResponse {
  pendingTransaction?: TransactionDetails
  transactions: Pageable<TransactionDetails>
}

export interface TransactionDetails {
  sourceAddress: string
  targetAddress: string
  value: number
  approvals: number
  requiredApprovals: number
  status: TransactionStatus
  dateCreated: Date
}

export enum TransactionStatus {
  DENIED = 'DENIED',
  APPROVED = 'APPROVED',
  PENDING = 'PENDING',
  FAILED = 'FAILED'
}

export interface NewTransactionInfo {
  bitcoinWalletBalance: BitcoinWalletBalance
  pendingExists: boolean
}

export interface TransactionForm {
  amount: number | undefined
  targetAddress: string
}

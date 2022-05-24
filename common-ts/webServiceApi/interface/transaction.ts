import {Pageable} from "./pageable";

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
    DENIED,
    APPROVED,
    PENDING,
    FAILED
}
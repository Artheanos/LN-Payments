import { TransactionStatus } from 'webService/interface/transaction'
import React from 'react'
import {
  Color,
  FailColor,
  PendingColor,
  StatusCell,
  SuccessColor
} from '../common/PageableTable/StatusCell'

const StatusColor: Record<TransactionStatus, Color> = {
  [TransactionStatus.PENDING]: PendingColor,
  [TransactionStatus.APPROVED]: SuccessColor,
  [TransactionStatus.FAILED]: FailColor,
  [TransactionStatus.DENIED]: FailColor
}

interface Props {
  transactionStatus: TransactionStatus
}

export const TransactionStatusCell: React.FC<Props> = ({
  transactionStatus
}) => (
  <StatusCell status={transactionStatus} colorMapper={StatusColor}></StatusCell>
)

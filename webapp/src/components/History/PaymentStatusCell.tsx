import React from 'react'
import { PaymentStatus } from 'webService/interface/payment'
import {
  Color,
  FailColor,
  PendingColor,
  StatusCell,
  SuccessColor
} from '../common/PageableTable/StatusCell'

const StatusColor: Record<PaymentStatus, Color> = {
  [PaymentStatus.PENDING]: PendingColor,
  [PaymentStatus.COMPLETE]: SuccessColor,
  [PaymentStatus.CANCELLED]: FailColor
}

interface Props {
  paymentStatus: PaymentStatus
}

export const PaymentStatusCell: React.FC<Props> = ({ paymentStatus }) => (
  <StatusCell colorMapper={StatusColor} status={paymentStatus} />
)

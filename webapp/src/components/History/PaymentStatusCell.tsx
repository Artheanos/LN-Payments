import React from 'react'
import { Typography } from '@mui/material'
import { PaymentStatus } from 'common-ts/dist/webServiceApi/interface/payment'
import {
  Color,
  FailColor,
  PendingColor,
  StatusCell,
  SuccessColor
} from '../common/StatusCell'

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

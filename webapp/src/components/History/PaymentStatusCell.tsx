import React from 'react'
import { Typography } from '@mui/material'
import { PaymentStatus } from 'common-ts/webServiceApi/interface/payment'

const StatusColor: Record<
  PaymentStatus,
  { primary: string; secondary: string }
> = {
  [PaymentStatus.PENDING]: {
    primary: '#4fc3f7',
    secondary: '#29b6f6'
  },
  [PaymentStatus.COMPLETE]: {
    primary: '#81c784',
    secondary: '#66bb6a'
  },
  [PaymentStatus.CANCELLED]: {
    primary: '#e57373',
    secondary: '#f44336'
  }
}

const calculateColor = (paymentStatus: PaymentStatus) => {
  const colors = StatusColor[paymentStatus]
  return {
    backgroundColor: colors.primary,
    borderColor: colors.secondary
  }
}

interface Props {
  paymentStatus: PaymentStatus
}

export const PaymentStatusCell: React.FC<Props> = ({ paymentStatus }) => {
  return (
    <Typography
      align="center"
      sx={{
        borderRadius: 8,
        fontWeight: 'bold',
        display: 'inline-block',
        fontSize: '0.75rem',
        padding: '3px 10px',
        borderStyle: 'solid',
        borderWidth: '3px',
        width: '100%',
        ...calculateColor(paymentStatus)
      }}
    >
      {paymentStatus}
    </Typography>
  )
}

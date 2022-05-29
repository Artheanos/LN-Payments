import { TransactionStatus } from 'common-ts/dist/webServiceApi/interface/transaction'
import { Typography } from '@mui/material'
import React from 'react'

const StatusColor: Record<
  TransactionStatus,
  { primary: string; secondary: string }
> = {
  [TransactionStatus.PENDING]: {
    primary: '#4fc3f7',
    secondary: '#29b6f6'
  },
  [TransactionStatus.APPROVED]: {
    primary: '#81c784',
    secondary: '#66bb6a'
  },
  [TransactionStatus.FAILED]: {
    primary: '#e57373',
    secondary: '#f44336'
  },
  [TransactionStatus.DENIED]: {
    primary: '#e57373',
    secondary: '#f44336'
  }
}

const calculateColor = (transactionStatus: TransactionStatus) => {
  console.log(transactionStatus)
  console.log(StatusColor)
  const colors = StatusColor[transactionStatus]
  console.log(colors)
  return {
    backgroundColor: colors.primary,
    borderColor: colors.secondary
  }
}

interface Props {
  paymentStatus: TransactionStatus
}

export const TransactionStatusCell: React.FC<Props> = ({ paymentStatus }) => {
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

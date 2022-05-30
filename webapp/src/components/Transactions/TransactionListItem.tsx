import React from 'react'
import { TableCell, TableRow, Typography } from '@mui/material'
import { TransactionDetails } from 'common-ts/dist/webServiceApi/interface/transaction'
import { TransactionStatusCell } from '../common/TransactionStatusCell'

export const TransactionListItem: React.FC<
  TransactionDetails & { highlighted?: boolean }
> = ({
  status,
  targetAddress,
  sourceAddress,
  dateCreated,
  requiredApprovals,
  approvals,
  value,
  highlighted
}) => {
  return (
    <TableRow className={highlighted ? 'bg-purple-100' : ''}>
      <TableCell>{new Date(dateCreated).toLocaleString()}</TableCell>
      <TableCell>{value}&nbsp;sats</TableCell>
      <TableCell>{sourceAddress}</TableCell>
      <TableCell>{targetAddress}</TableCell>
      <TableCell>
        <TransactionStatusCell transactionStatus={status} />
      </TableCell>
      <TableCell className="font-bold">
        <Typography
          sx={{
            fontStyle: 'italic',
            fontWeight: 'bold'
          }}
        >
          {approvals}/{requiredApprovals}
        </Typography>
      </TableCell>
    </TableRow>
  )
}

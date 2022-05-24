import React from 'react'
import { TableCell, TableRow } from '@mui/material'
import { TransactionDetails } from 'common-ts/dist/webServiceApi/interface/transaction'

interface Props {
  transaction: TransactionDetails
}

export const TransactionListItem: React.FC<Props> = ({ transaction }) => {
  return (
    <TableRow>
      <TableCell>{transaction.sourceAddress}</TableCell>
      <TableCell>{transaction.targetAddress}</TableCell>
    </TableRow>
  )
}

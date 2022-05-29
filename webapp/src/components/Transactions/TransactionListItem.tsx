import React from 'react'
import {TableCell, TableRow} from '@mui/material'
import {TransactionDetails} from 'common-ts/dist/webServiceApi/interface/transaction'

export const TransactionListItem: React.FC<TransactionDetails> = ({
                                                                    status,
                                                                    targetAddress,
                                                                    sourceAddress,
                                                                    dateCreated,
                                                                    requiredApprovals,
                                                                    approvals,
                                                                    value
                                                                  }) => {
  return (
    <TableRow className="bg-yellow-100">
      <TableCell>{dateCreated}</TableCell>
      <TableCell>{value} sat</TableCell>
      <TableCell>{sourceAddress}</TableCell>
      <TableCell>{targetAddress}</TableCell>
      <TableCell>{status}</TableCell>
      <TableCell>{approvals}/{requiredApprovals}</TableCell>
    </TableRow>
  )
}

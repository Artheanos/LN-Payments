import React, { ChangeEvent, useEffect, useState } from 'react'
import { api } from '../../api'
import {
  LinearProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableFooter,
  TableHead,
  TablePagination,
  TableRow,
  Tooltip
} from '@mui/material'
import TablePaginationActions from '@mui/material/TablePagination/TablePaginationActions'

export const History: React.FC = () => {
  const [history, setHistory] = useState<PaymentHistory>()
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    queryHistory()
  }, [])

  const queryHistory = (page = 0, size = 20) => {
    setLoading(true)
    api.payment
      .history({ page, size })
      .then((data) => {
        if (data.status === 200) {
          console.log(data.data)
          setLoading(false)
          setHistory(data.data)
        }
      })
      .catch((err) => {
        console.log(err)
      })
  }

  const handleChangePage = (
    event: React.MouseEvent<HTMLButtonElement, MouseEvent> | null,
    newPage: number
  ) => {
    if (history) {
      queryHistory(newPage, history?.pageable.pageSize)
    }
  }

  const handleChangeRowsPerPage = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    if (history) {
      queryHistory(0, parseInt(event!.currentTarget.value))
    }
  }
  if (loading) return <LinearProgress />
  return (
    <div className="grow p-14 w-6/12 text-center">
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow
              sx={{
                ['& .MuiTableCell-root']: { fontWeight: 'bold' }
              }}
            >
              <TableCell>Payment Request</TableCell>
              <TableCell>Created</TableCell>
              <TableCell>Price</TableCell>
              <TableCell>Number of tokens</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Tokens</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {history?.content.map((payment: PaymentDetails, key: number) => (
              <TableRow key={key}>
                <Tooltip arrow title={payment.paymentRequest}>
                  <TableCell className="max-w-sm truncate">
                    {payment.paymentRequest}
                  </TableCell>
                </Tooltip>
                <TableCell>
                  {new Date(payment.timestamp).toLocaleString()}
                </TableCell>
                <TableCell>{payment.price}</TableCell>
                <TableCell>{payment.numberOfTokens}</TableCell>
                <TableCell>{payment.paymentStatus}</TableCell>
                <TableCell>{payment.tokens}</TableCell>
              </TableRow>
            ))}
          </TableBody>
          {history && (
            <TableFooter>
              <TableRow>
                <TablePagination
                  rowsPerPageOptions={[2, 10, 20, { label: 'All', value: -1 }]}
                  count={history!.totalElements}
                  rowsPerPage={history!.pageable.pageSize}
                  page={history!.pageable.pageNumber}
                  onPageChange={handleChangePage}
                  onRowsPerPageChange={handleChangeRowsPerPage}
                  ActionsComponent={TablePaginationActions}
                />
              </TableRow>
            </TableFooter>
          )}
        </Table>
      </TableContainer>
    </div>
  )
}

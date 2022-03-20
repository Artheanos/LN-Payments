import React, { useEffect, useState } from 'react'
import { api } from '../../api'
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableFooter,
  TableHead, TablePagination,
  TableRow
} from '@mui/material'
import TablePaginationActions from '@mui/material/TablePagination/TablePaginationActions'

export const History: React.FC = () => {
  const [history, setHistory] = useState<PaymentHistory>()

  useEffect(() => {
    api.payment.history().then((data) => {
      if (data.status === 200) {
        console.log(data.data)
        setHistory(data.data)
      }
    })
  }, [])

  function handleChangePage() {

  }

  function handleChangeRowsPerPage() {

  }

  return (
    <div className="p-14 w-full text-center">
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>asd kk</TableCell>
              <TableCell>asd kk</TableCell>
              <TableCell>asd kk</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {history?.content.map((payment: PaymentDetails, key: number) => (
              <TableRow key={key}>
                <TableCell>{payment.paymentRequest.slice(0, 8)}</TableCell>
                <TableCell>{payment.price}</TableCell>
                <TableCell>{payment.numberOfTokens}</TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TablePagination
                rowsPerPageOptions={[5, 10, 25, { label: 'All', value: -1 }]}
                colSpan={3}
                count={history!.numberOfElements}
                rowsPerPage={history!.pageable.pageSize}
                page={history!.pageable.pageNumber}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
                ActionsComponent={TablePaginationActions}
              />
            </TableRow>
          </TableFooter>
        </Table>
      </TableContainer>
    </div>
  )
}

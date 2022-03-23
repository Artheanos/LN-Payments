import React, { ChangeEvent, useEffect, useState } from 'react'
import { api } from '../../api'
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableFooter,
  TableHead,
  TablePagination,
  TableRow,
  Toolbar
} from '@mui/material'
import { PaymentEntry } from './PaymentEntry'

export const History: React.FC = () => {
  const [history, setHistory] = useState<PaymentHistory>()

  useEffect(() => {
    queryHistory()
  }, [])

  const queryHistory = (page = 0, size = 10) => {
    api.payment
      .history({ page, size })
      .then((data) => {
        if (data.status === 200) {
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
      queryHistory(0, parseInt(event!.target.value))
    }
  }

  return (
    <div className="overflow-y-auto grow py-8 px-14 text-center">
      <Toolbar />
      {!history || history.empty ? (
        <p className="italic text-gray-500">No payments found!</p>
      ) : (
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
                <PaymentEntry key={key} {...payment} />
              ))}
            </TableBody>
            {history && (
              <TableFooter>
                <TableRow>
                  <TablePagination
                    rowsPerPageOptions={[
                      2,
                      10,
                      20,
                      { label: 'All', value: -1 }
                    ]}
                    count={history!.totalElements}
                    rowsPerPage={history!.pageable.pageSize}
                    page={history!.pageable.pageNumber}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                  />
                </TableRow>
              </TableFooter>
            )}
          </Table>
        </TableContainer>
      )}
    </div>
  )
}

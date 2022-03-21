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
  Tooltip,
  Typography
} from '@mui/material'
import TablePaginationActions from '@mui/material/TablePagination/TablePaginationActions'
import VisibilityIcon from '@mui/icons-material/Visibility'

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
          setLoading(false)
          setHistory(data.data)
        }
      })
      .catch((err) => {
        console.log(err)
      })
  }

  const StatusColor: Record<
    string,
    {
      primary: string
      secondary: string
    }
  > = {
    PENDING: {
      primary: '#4fc3f7',
      secondary: '#29b6f6'
    },
    COMPLETE: {
      primary: '#81c784',
      secondary: '#66bb6a'
    },
    CANCELLED: {
      primary: '#e57373',
      secondary: '#f44336'
    }
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

  if (loading) return <LinearProgress />

  const calculateColor = (paymentStatus: PaymentStatus) => {
    const colors = StatusColor[paymentStatus as unknown as string]
    return {
      backgroundColor: colors.primary,
      borderColor: colors.secondary
    }
  }

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
                <TableCell>
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
                      ...calculateColor(payment.paymentStatus)
                    }}
                  >
                    {payment.paymentStatus}
                  </Typography>
                </TableCell>
                <TableCell align="center">
                  {payment.tokens ? (
                    <Typography
                      sx={{
                        cursor: 'pointer',
                        fontSize: '0.75rem',
                        display: 'flex',
                        alignItems: 'center',
                        color: '#0d6efd'
                      }}
                    >
                      <VisibilityIcon />
                      <span className="pl-1">Show</span>
                    </Typography>
                  ) : (
                    <Typography
                      sx={{
                        fontStyle: 'italic',
                        fontSize: '0.85rem',
                        color: 'darkgray'
                      }}
                    >
                      none
                    </Typography>
                  )}
                </TableCell>
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

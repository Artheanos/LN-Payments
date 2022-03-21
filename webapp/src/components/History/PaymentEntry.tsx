import React from 'react'
import { TableCell, TableRow, Tooltip, Typography } from '@mui/material'
import VisibilityIcon from '@mui/icons-material/Visibility'
import { TokenPopup } from './TokenPopup'

export const PaymentEntry: React.FC<PaymentDetails> = (
  payment: PaymentDetails
) => {
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

  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null)

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget)
  }

  const handleClose = () => {
    setAnchorEl(null)
  }

  const calculateColor = (paymentStatus: PaymentStatus) => {
    const colors = StatusColor[paymentStatus as unknown as string]
    return {
      backgroundColor: colors.primary,
      borderColor: colors.secondary
    }
  }
  return (
    <>
      <TableRow>
        <Tooltip arrow title={payment.paymentRequest}>
          <TableCell className="max-w-sm truncate">
            {payment.paymentRequest}
          </TableCell>
        </Tooltip>
        <TableCell>{new Date(payment.timestamp).toLocaleString()}</TableCell>
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
              onClick={handleClick}
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
      <TokenPopup
        id={payment.paymentRequest}
        anchorEl={anchorEl}
        handleClose={handleClose}
        tokens={[
          {
            sequence: 'ddddawdawdavdfv'
          },
          {
            sequence: 'ddddawdawdavdfv'
          }
        ]}
      />
    </>
  )
}

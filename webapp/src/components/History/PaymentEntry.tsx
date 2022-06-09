import React from 'react'
import VisibilityIcon from '@mui/icons-material/Visibility'
import { TableCell, TableRow, Tooltip, Typography } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { PaymentDetails } from 'webService/interface/payment'
import { PaymentStatusCell } from './PaymentStatusCell'
import { TokenPopup } from './TokenPopup'

export const PaymentEntry: React.FC<PaymentDetails> = (
  payment: PaymentDetails
) => {
  const { t } = useTranslation('history')

  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null)

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget)
  }

  const handleClose = () => {
    setAnchorEl(null)
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
          <PaymentStatusCell paymentStatus={payment.paymentStatus} />
        </TableCell>
        <TableCell align="center">
          {payment.tokens.length ? (
            <Typography
              onClick={handleClick}
              sx={{
                cursor: 'pointer',
                fontSize: '0.75rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#0d6efd'
              }}
            >
              <VisibilityIcon />
              <span className="pl-1">{t('show')}</span>
            </Typography>
          ) : (
            <Typography
              sx={{
                fontStyle: 'italic',
                fontSize: '0.85rem',
                color: 'darkgray'
              }}
            >
              {t('none')}
            </Typography>
          )}
        </TableCell>
      </TableRow>
      <TokenPopup
        id={payment.paymentRequest}
        anchorEl={anchorEl}
        handleClose={handleClose}
        tokens={payment.tokens}
      />
    </>
  )
}

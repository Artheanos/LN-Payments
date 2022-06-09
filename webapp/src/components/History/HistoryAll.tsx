import React from 'react'
import { useTranslation } from 'react-i18next'

import { api } from 'webService/requests'
import { TableCell, TableRow } from '@mui/material'
import { PaymentStatusCell } from './PaymentStatusCell'
import { PublicPaymentDetails } from 'webService/interface/payment'
import { ApiPageableTable } from '../common/PageableTable/ApiPageableTable'

export const HistoryAll: React.FC = () => {
  const { t } = useTranslation('history')

  const headers = [
    t('common:email'),
    t('created'),
    t('price'),
    t('numberOfTokens'),
    t('status')
  ]

  return (
    <ApiPageableTable
      apiRequest={api.payment.historyAll}
      mapper={(value: PublicPaymentDetails, key: number) => (
        <TableRow key={key}>
          <TableCell>{value.email}</TableCell>
          <TableCell>{value.timestamp}</TableCell>
          <TableCell>{value.price}</TableCell>
          <TableCell>{value.numberOfTokens}</TableCell>
          <TableCell className="w-32">
            <PaymentStatusCell paymentStatus={value.paymentStatus} />
          </TableCell>
        </TableRow>
      )}
      headers={headers}
    />
  )
}

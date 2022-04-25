import React from 'react'
import { useTranslation } from 'react-i18next'

import { PageableTable } from 'components/common/PageableTable/PageableTable'
import { api } from 'api'
import { TableCell, TableRow } from '@mui/material'
import { PaymentStatusCell } from './PaymentStatusCell'

export const HistoryAll: React.FC = () => {
  const { t } = useTranslation('common')

  const headers = [
    t('common:email'),
    t('history.created'),
    t('history.price'),
    t('history.numberOfTokens'),
    t('history.status')
  ]

  return (
    <PageableTable
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

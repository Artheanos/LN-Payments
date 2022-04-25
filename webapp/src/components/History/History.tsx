import React from 'react'
import { useTranslation } from 'react-i18next'

import { PageableTable } from 'components/common/PageableTable/PageableTable'
import { PaymentEntry } from './PaymentEntry'
import { api } from 'api'

export const History: React.FC = () => {
  const { t } = useTranslation('common')

  const headers = [
    t('history.paymentRequest'),
    t('history.created'),
    t('history.price'),
    t('history.numberOfTokens'),
    t('history.status'),
    t('history.tokens')
  ]

  return (
    <PageableTable
      apiRequest={api.payment.history}
      mapper={(value: PaymentDetails, key: number) => (
        <PaymentEntry {...value} key={key} />
      )}
      headers={headers}
    />
  )
}

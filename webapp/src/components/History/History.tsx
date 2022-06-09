import React from 'react'
import { useTranslation } from 'react-i18next'

import { ApiPageableTable } from '../common/PageableTable/ApiPageableTable'
import { PaymentDetails } from 'webService/interface/payment'
import { PaymentEntry } from './PaymentEntry'
import { api } from 'webService/requests'

export const History: React.FC = () => {
  const { t } = useTranslation('history')

  const headers = [
    t('paymentRequest'),
    t('created'),
    t('price'),
    t('numberOfTokens'),
    t('status'),
    t('tokens')
  ]

  return (
    <ApiPageableTable
      apiRequest={api.payment.history}
      mapper={(value: PaymentDetails, key: number) => (
        <PaymentEntry {...value} key={key} />
      )}
      headers={headers}
    />
  )
}

import React from 'react'
import { useTranslation } from 'react-i18next'

import { PaymentEntry } from './PaymentEntry'
import { api } from 'api'
import { PaymentDetails } from 'common-ts/dist/webServiceApi/interface/payment'
import { ApiPageableTable } from '../common/PageableTable/ApiPageableTable'

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

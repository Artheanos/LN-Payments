import React from 'react'
import { useTranslation } from 'react-i18next'

import { PageableTable } from 'components/common/PageableTable/PageableTable'
import { PaymentEntry } from './PaymentEntry'
import { api } from 'api'
import { PaymentDetails } from 'common-ts/webServiceApi/interface/payment'

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
    <PageableTable
      apiRequest={api.payment.history}
      mapper={(value: PaymentDetails, key: number) => (
        <PaymentEntry {...value} key={key} />
      )}
      headers={headers}
    />
  )
}

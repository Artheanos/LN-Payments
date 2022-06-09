import React, { useCallback, useEffect, useState } from 'react'
import Panel from 'components/common/Panel'
import { PageableTable } from 'components/common/PageableTable/PageableTable'
import { api } from 'webService/requests'
import { TransactionListItem } from 'components/Transactions/TransactionListItem'
import {
  TransactionDetails,
  TransactionsResponse
} from 'webService/interface/transaction'
import { Button } from '@mui/material'
import { Link } from 'react-router-dom'
import routesBuilder from 'routesBuilder'
import { useTranslation } from 'react-i18next'

export const TransactionsPage: React.FC = () => {
  const { t } = useTranslation('transactions')
  const [elements, setElements] = useState<TransactionsResponse>()
  const [loading, setLoading] = useState(true)

  const queryElements = useCallback(async (page = 0, size = 10) => {
    setLoading(true)
    const { data } = await api.transactions.getTransactions({ page, size })
    if (data) {
      setElements(data)
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    queryElements()
  }, [queryElements])
  return (
    <Panel.Container>
      <Panel.Header title={t('header')}>
        <div>
          {!elements ||
            (!elements?.pendingTransaction && (
              <Link to={routesBuilder.userPanel.transactions.new}>
                <Button variant="contained">{t('createHeader')}</Button>
              </Link>
            ))}
        </div>
      </Panel.Header>
      <Panel.Body table>
        <PageableTable
          queryElements={queryElements}
          loading={loading}
          pageElements={elements?.transactions}
          distinguishedData={elements?.pendingTransaction}
          mapper={(
            transaction: TransactionDetails,
            key,
            highlighted?: boolean
          ) => (
            <TransactionListItem
              {...transaction}
              key={key}
              highlighted={highlighted}
            />
          )}
          headers={[
            t('date'),
            t('value'),
            t('sourceAddress'),
            t('targetAddress'),
            t('status'),
            t('approvals')
          ]}
        />
      </Panel.Body>
    </Panel.Container>
  )
}

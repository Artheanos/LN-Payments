import React, { useCallback, useEffect, useState } from 'react'
import Panel from 'components/common/Panel'
import { PageableTable } from 'components/common/PageableTable/PageableTable'
import { api } from 'api'
import { TransactionListItem } from 'components/Transactions/TransactionListItem'
import {
  TransactionDetails,
  TransactionsResponse
} from 'common-ts/dist/webServiceApi/interface/transaction'

export const TransactionsPage: React.FC = () => {
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
      <Panel.Header title="Transactions" />
      <Panel.Body table>
        <PageableTable
          pageElements={elements?.transactions}
          mapper={(transaction: TransactionDetails, key) => (
            <TransactionListItem transaction={transaction} key={key} />
          )}
          headers={['test1', 'test2']}
        />
      </Panel.Body>
    </Panel.Container>
  )
}

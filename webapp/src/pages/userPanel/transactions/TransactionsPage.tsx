import React, {useCallback, useEffect, useState} from 'react'
import Panel from 'components/common/Panel'
import {PageableTable} from 'components/common/PageableTable/PageableTable'
import {api} from 'api'
import {TransactionListItem} from 'components/Transactions/TransactionListItem'
import {
  TransactionDetails,
  TransactionsResponse
} from 'common-ts/dist/webServiceApi/interface/transaction'
import {Box, Button, Grid} from "@mui/material";
import {WalletCard} from "../../../components/wallet/WalletCard";
import {Link} from "react-router-dom";
import routesBuilder from "../../../routesBuilder";

export const TransactionsPage: React.FC = () => {
  const [elements, setElements] = useState<TransactionsResponse>()
  const [loading, setLoading] = useState(true)

  const queryElements = useCallback(async (page = 0, size = 10) => {
    setLoading(true)
    const {data} = await api.transactions.getTransactions({page, size})
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
      <Panel.Header title="Transactions">
        <Link to={routesBuilder.userPanel.transactions.new}>
          <Button variant="contained">Create transaction</Button>
        </Link>
      </Panel.Header>
      <Panel.Body table>
        <PageableTable
          pageElements={elements?.transactions}
          distinguishedRow={<TransactionListItem {...elements?.pendingTransaction!}/>}
          mapper={(transaction: TransactionDetails, key) => (
            <TransactionListItem {...transaction} key={key}/>
          )}
          headers={['Date', 'Value', 'Source', 'Target', 'Status', 'Approvals']}
        />
      </Panel.Body>
    </Panel.Container>
  )
}

import React, {useCallback, useEffect, useState} from 'react'
import Panel from 'components/common/Panel'
import {PageableTable} from 'components/common/PageableTable/PageableTable'
import {api} from 'api'
import {TransactionListItem} from 'components/Transactions/TransactionListItem'
import {
  TransactionDetails,
  TransactionsResponse
} from 'common-ts/dist/webServiceApi/interface/transaction'
import {Box, Grid} from "@mui/material";
import {WalletCard} from "../../../components/wallet/WalletCard";

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
    <Grid className="text-center" container spacing={3}>
      <WalletCard standardSize={12}>
        <Box className="flex justify-between items-baseline">
          <span className="text-2xl font-bold">{'Transactions'}</span>
        </Box>
      </WalletCard>
      <WalletCard standardSize={12}>
        <Box className="flex justify-between items-baseline">
          <span className="text-2xl font-bold">{'Transactions'}</span>
        </Box>
      </WalletCard>
      <Grid item md={12}>
        <Box className={"flex !flex-col justify-around space-y-10 h-full"}>
          <Panel.Container className={"pt-7"}>
            <Panel.Body table>
              <PageableTable
                pageElements={elements?.transactions}
                mapper={(transaction: TransactionDetails, key) => (
                  <TransactionListItem {...transaction} key={key}/>
                )}
                headers={['Date', 'Value', 'Source', 'Target', 'Status', 'Approvals']}
              />
            </Panel.Body>
          </Panel.Container>
        </Box>
      </Grid>
    </Grid>
  )
}

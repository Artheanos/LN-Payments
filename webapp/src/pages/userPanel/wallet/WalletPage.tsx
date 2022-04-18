import React, { useCallback, useEffect, useState } from 'react'
import { Grid } from '@mui/material'
import { useNavigate } from 'react-router-dom'

import { api } from 'api'
import { useNotification } from 'components/Context/NotificationContext'
import { WalletLoadingSkeleton } from './WalletLoadingSkeleton'
import routesBuilder from 'routesBuilder'
import { WalletCard } from 'components/Wallet/WalletCard'
import { BitcoinWalletCard } from 'components/Wallet/BitcoinWalletCard'
import {ChannelsBalanceCard} from "../../../components/Wallet/ChannelsBalanceCard";

export const WalletPage: React.FC = () => {
  const navigate = useNavigate()
  const notify = useNotification()
  const [loading, setLoading] = useState(true)
  const [walletInfo, setWalletInfo] = useState<WalletInfo>()

  const onData = useCallback(
    (status: number, data: WalletInfo | undefined) => {
      if (status === 404) {
        notify('Redirected to wallet creation form', 'info')
        navigate(routesBuilder.userPanel.wallet.new)
        return
      }
      if (status === 200) {
        setWalletInfo(data)
        setLoading(false)
        return
      }
    },
    [navigate, notify]
  )

  useEffect(() => {
    let isMounted = true

    api.wallet.getInfo().then(({ status, data }) => {
      if (!isMounted) return

      onData(status, data)
    })
    return () => {
      isMounted = false
    }
  }, [onData])

  if (loading) return <WalletLoadingSkeleton />

  return (
    <Grid className="text-center" container spacing={3}>
      <BitcoinWalletCard {...walletInfo!.bitcoinWalletBalance} />
      <ChannelsBalanceCard {...walletInfo!.channelsBalance} />
      <WalletCard standardSize={9}>
        <img
          src="https://peltiertech.com/images/2010-08/LineChart01.png"
          alt="chart"
        />
      </WalletCard>
    </Grid>
  )
}

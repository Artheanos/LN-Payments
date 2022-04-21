import React, { useCallback, useEffect, useState } from 'react'
import { Box, Button, Grid, Paper } from '@mui/material'
import { Link, useNavigate } from 'react-router-dom'

import { api } from 'api'
import { useNotification } from 'components/Context/NotificationContext'
import { WalletLoadingSkeleton } from './WalletLoadingSkeleton'
import routesBuilder from 'routesBuilder'
import { WalletCard } from 'components/Wallet/WalletCard'
import { BitcoinWalletCard } from 'components/Wallet/BitcoinWalletCard'
import { ChannelsBalanceCard } from 'components/Wallet/ChannelsBalanceCard'
import { LightningWalletCard } from 'components/Wallet/LightningWalletCard'
import { ActionsCard } from 'components/Wallet/ActionsCard'
import Panel from '../../../components/common/Panel'

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
      <WalletCard standardSize={12}>
        <Box className="flex justify-between items-baseline">
          <span className="text-2xl font-bold">Wallet</span>
          <span>{walletInfo!.address}</span>
        </Box>
      </WalletCard>
      <BitcoinWalletCard {...walletInfo!.bitcoinWalletBalance} />
      <ChannelsBalanceCard {...walletInfo!.channelsBalance} />
      <LightningWalletCard {...walletInfo!.lightningWalletBalance} />
      <WalletCard standardSize={9}>
        <img
          className="h-60"
          src="https://peltiertech.com/images/2010-08/LineChart01.png"
          alt="chart"
        />
      </WalletCard>
      <ActionsCard
        channelsBalance={walletInfo!.channelsBalance.totalBalance}
        lightningWalletBalance={
          walletInfo!.lightningWalletBalance.availableBalance
        }
      />
    </Grid>
  )
}

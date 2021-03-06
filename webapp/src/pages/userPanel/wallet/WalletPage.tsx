import React, { useCallback, useEffect, useState } from 'react'
import { Box, Grid } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'
import { ActionsCard } from 'components/wallet/ActionsCard'
import { BitcoinWalletCard } from 'components/wallet/BitcoinWalletCard'
import { ChannelsBalanceCard } from 'components/wallet/ChannelsBalanceCard'
import { LightningWalletCard } from 'components/wallet/LightningWalletCard'
import { TotalIncomeChart } from 'components/wallet/TotalIncomeChart'
import { WalletCard } from 'components/wallet/WalletCard'
import { WalletInfo } from 'webService/interface/wallet'
import { WalletLoadingSkeleton } from 'components/wallet/WalletLoadingSkeleton'
import { api } from 'webService/requests'
import { useNotification } from 'components/Context/NotificationContext'

export const WalletPage: React.FC = () => {
  const { t } = useTranslation('wallet')
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
          <span className="text-2xl font-bold">{t('title')}</span>
          <span>{walletInfo!.address}</span>
        </Box>
      </WalletCard>
      <BitcoinWalletCard {...walletInfo!.bitcoinWalletBalance} />
      <ChannelsBalanceCard {...walletInfo!.channelsBalance} />
      <LightningWalletCard {...walletInfo!.lightningWalletBalance} />
      <TotalIncomeChart chartData={walletInfo?.totalIncomeData || []} />
      <ActionsCard
        channelsBalance={walletInfo!.channelsBalance.totalBalance}
        lightningWalletBalance={
          walletInfo!.lightningWalletBalance.availableBalance
        }
      />
    </Grid>
  )
}

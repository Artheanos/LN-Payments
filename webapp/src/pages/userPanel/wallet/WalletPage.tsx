import React from 'react'
import { Box, Grid } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { ActionsCard } from 'components/wallet/ActionsCard'
import { BitcoinWalletCard } from 'components/wallet/BitcoinWalletCard'
import { ChannelsBalanceCard } from 'components/wallet/ChannelsBalanceCard'
import { LightningWalletCard } from 'components/wallet/LightningWalletCard'
import { TotalIncomeChart } from 'components/wallet/TotalIncomeChart'
import { WalletCard } from 'components/wallet/WalletCard'
import { WalletInfo } from 'webService/interface/wallet'

type Props = {
  walletInfo?: WalletInfo
}

export const WalletPage: React.FC<Props> = ({ walletInfo }) => {
  const { t } = useTranslation('wallet')

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

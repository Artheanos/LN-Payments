import React from 'react'
import { WalletCard } from './WalletCard'
import { Box, LinearProgress, Tooltip } from '@mui/material'
import { useTranslation } from 'react-i18next'
import {BalanceProgressBar} from "./BalanceProgressBar";

export const ChannelsBalanceCard: React.FC<ChannelsBalance> = ({
  totalBalance,
  openedChannels,
  autoChannelCloseLimit
}) => {
  const { t } = useTranslation('common')

  return (
    <WalletCard standardSize={4}>
      <span className="text-xl font-bold">
        {t('wallet.channelsBalance.header')}
      </span>
      <span className="text-3xl font-extrabold text-pink-600">
        {totalBalance.toLocaleString() + ' sats'}
      </span>
      <BalanceProgressBar
        tooltipContent={t('wallet.channelsBalance.tooltipContent')}
        maxValue={autoChannelCloseLimit}
        color="secondary"
        balance={totalBalance}
      />
      <span className="text-slate-500">
        {openedChannels + t('wallet.channelsBalance.openChannels')}
      </span>
    </WalletCard>
  )
}

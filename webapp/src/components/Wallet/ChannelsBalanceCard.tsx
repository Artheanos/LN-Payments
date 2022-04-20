import React from 'react'
import { useTranslation } from 'react-i18next'
import { ProgressCard } from './ProgressCard'

export const ChannelsBalanceCard: React.FC<ChannelsBalance> = ({
  totalBalance,
  openedChannels,
  autoChannelCloseLimit
}) => {
  const { t } = useTranslation('common')

  return (
    <ProgressCard
      headerText={t('wallet.channelsBalance.header')}
      value={totalBalance}
      maxValue={autoChannelCloseLimit}
      tooltipContent={t('wallet.channelsBalance.tooltipContent')}
      bottomText={openedChannels + t('wallet.channelsBalance.openChannels')}
      unit="sats"
      color="secondary"
    />
  )
}

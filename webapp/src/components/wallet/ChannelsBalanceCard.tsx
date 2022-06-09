import { ChannelsBalance } from 'webService/interface/wallet'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { ProgressCard } from './ProgressCard'

export const ChannelsBalanceCard: React.FC<ChannelsBalance> = ({
  totalBalance,
  openedChannels,
  autoChannelCloseLimit
}) => {
  const { t } = useTranslation('wallet')

  return (
    <ProgressCard
      headerText={t('channelsBalance.header')}
      value={totalBalance}
      maxValue={autoChannelCloseLimit}
      tooltipContent={t('channelsBalance.tooltipContent')}
      bottomText={openedChannels + t('channelsBalance.openChannels')}
      unit="sats"
      color="secondary"
    />
  )
}

import React from 'react'
import { WalletCard } from './WalletCard'
import { Box, LinearProgress, Tooltip } from '@mui/material'
import { useTranslation } from 'react-i18next'

export const ChannelsBalanceCard: React.FC<ChannelsBalance> = ({
  totalBalance,
  openedChannels,
  autoChannelCloseLimit
}) => {
  const { t } = useTranslation('common')

  const normalise = (value: number): number =>
    (value * 100) / autoChannelCloseLimit

  return (
    <WalletCard standardSize={4}>
      <span className="text-xl font-bold">
        {t('wallet.channelsBalance.header')}
      </span>
      <span className="text-3xl font-extrabold text-pink-600">
        {totalBalance.toLocaleString() + ' sats'}
      </span>
      <Tooltip title={t('wallet.channelsBalance.tooltipContent') as string}>
        <Box>
          <LinearProgress
            value={normalise(totalBalance)}
            variant="determinate"
            color="secondary"
            sx={{
              height: 16,
              borderRadius: 5
            }}
          />
          <Box className="flex justify-between text-slate-500">
            <span>0</span>
            <span>{autoChannelCloseLimit.toLocaleString()}</span>
          </Box>
        </Box>
      </Tooltip>
      <span className="text-slate-500">
        {openedChannels + t('wallet.channelsBalance.openChannels')}
      </span>
    </WalletCard>
  )
}

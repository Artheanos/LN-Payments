import React from 'react'
import { WalletCard } from './WalletCard'
import { Box, LinearProgress, Tooltip } from '@mui/material'
import { useTranslation } from 'react-i18next'
import {BalanceProgressBar} from "./BalanceProgressBar";

export const LightningWalletCard: React.FC<LightningWalletBalance> = ({
  availableBalance,
  unconfirmedBalance,
  autoTransferLimit
}) => {
  const { t } = useTranslation('common')

  return (
    <WalletCard standardSize={4}>
      <span className="text-xl font-bold">
        {t('wallet.lightningWallet.header')}
      </span>
      <span className="text-3xl font-extrabold text-purple-700">
        {availableBalance.toLocaleString() + ' sats'}
      </span>
      <BalanceProgressBar
        tooltipContent={t('wallet.lightningWallet.tooltipContent')}
        maxValue={autoTransferLimit}
        color="primary"
        balance={availableBalance}
      />
      <span className="text-slate-500">
        {unconfirmedBalance.toLocaleString() +
          ' sats' +
          t('wallet.unconfirmed')}
      </span>
    </WalletCard>
  )
}

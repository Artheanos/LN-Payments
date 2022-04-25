import React from 'react'
import { useTranslation } from 'react-i18next'
import { ProgressCard } from './ProgressCard'

export const LightningWalletCard: React.FC<LightningWalletBalance> = ({
  availableBalance,
  unconfirmedBalance,
  autoTransferLimit
}) => {
  const { t } = useTranslation('common')

  return (
    <ProgressCard
      headerText={t('wallet.lightningWallet.header')}
      value={availableBalance}
      maxValue={autoTransferLimit}
      tooltipContent={t('wallet.lightningWallet.tooltipContent')}
      bottomText={
        unconfirmedBalance.toLocaleString() + ' sats' + t('wallet.unconfirmed')
      }
      unit="sats"
      color="primary"
    />
  )
}

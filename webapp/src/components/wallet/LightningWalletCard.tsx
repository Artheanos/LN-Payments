import React from 'react'
import { useTranslation } from 'react-i18next'

import { ProgressCard } from './ProgressCard'
import { LightningWalletBalance } from 'webService/interface/wallet'

export const LightningWalletCard: React.FC<LightningWalletBalance> = ({
  availableBalance,
  unconfirmedBalance,
  autoTransferLimit
}) => {
  const { t } = useTranslation('wallet')

  return (
    <ProgressCard
      headerText={t('lightningWallet.header')}
      value={availableBalance}
      maxValue={autoTransferLimit}
      tooltipContent={t('lightningWallet.tooltipContent')}
      bottomText={
        unconfirmedBalance.toLocaleString() + ' sats' + t('unconfirmed')
      }
      unit="sats"
      color="primary"
    />
  )
}

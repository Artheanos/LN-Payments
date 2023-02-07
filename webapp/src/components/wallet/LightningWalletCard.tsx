import React from 'react'
import { useTranslation } from 'react-i18next'

import { ProgressCard } from './ProgressCard'
import { LightningWalletBalance } from 'webService/interface/wallet'

export const LightningWalletCard: React.FC<
  LightningWalletBalance & { autopilotEnabled: boolean }
> = ({
  availableBalance,
  unconfirmedBalance,
  autoTransferLimit,
  autopilotEnabled
}) => {
  const { t } = useTranslation('wallet')

  return (
    <ProgressCard
      headerText={t('lightningWallet.header')}
      value={availableBalance}
      maxValue={autoTransferLimit}
      tooltipContent={t('lightningWallet.tooltipContent')}
      bottomText={`${unconfirmedBalance.toLocaleString()} sats${t(
        'unconfirmed'
      )}\nAutopilot ${autopilotEnabled ? 'enabled' : 'disabled'}`}
      unit="sats"
      color="primary"
    />
  )
}

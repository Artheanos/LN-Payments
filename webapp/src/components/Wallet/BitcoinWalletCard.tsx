import { WalletCard } from './WalletCard'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { Typography } from '@mui/material'

export const BitcoinWalletCard: React.FC<BitcoinWalletBalance> = ({
  availableBalance,
  unconfirmedBalance
}) => {
  const { t } = useTranslation('common')

  return (
    <WalletCard standardSize={4}>
      <span className="text-xl font-bold">
        {t('wallet.bitcoinWallet.header')}
      </span>
      <Typography color="primary">
        <span className="text-3xl font-extrabold">
          {availableBalance.toLocaleString()} sats
        </span>
      </Typography>
      <hr />
      <span className="text-slate-500">
        {unconfirmedBalance.toLocaleString() +
          ' sats' +
          t('wallet.unconfirmed')}
      </span>
    </WalletCard>
  )
}

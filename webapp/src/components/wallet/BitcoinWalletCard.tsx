import { WalletCard } from './WalletCard'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { Typography } from '@mui/material'
import { BitcoinWalletBalance } from 'webService/interface/wallet'

export const BitcoinWalletCard: React.FC<BitcoinWalletBalance> = ({
  availableBalance,
  unconfirmedBalance
}) => {
  const { t } = useTranslation('wallet')

  return (
    <WalletCard standardSize={4}>
      <span className="text-xl font-bold">{t('bitcoinWallet.header')}</span>
      <Typography color="primary">
        <span className="text-3xl font-extrabold">
          {availableBalance.toLocaleString()} sats
        </span>
      </Typography>
      <hr />
      <span className="text-slate-500">
        {unconfirmedBalance.toLocaleString() + ' sats' + t('unconfirmed')}
      </span>
    </WalletCard>
  )
}

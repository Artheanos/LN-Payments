import { WalletCard } from './WalletCard'
import React from 'react'
import { useTranslation } from 'react-i18next'

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
      <span className="text-3xl font-extrabold text-purple-700">
        {availableBalance.toLocaleString()} sats
      </span>
      <hr />
      <span className="text-slate-500">
        {unconfirmedBalance.toLocaleString() +
          ' sats' +
          t('wallet.unconfirmed')}
      </span>
    </WalletCard>
  )
}

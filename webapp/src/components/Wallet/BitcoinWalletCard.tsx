import { WalletCard } from './WalletCard'
import React from 'react'

export const BitcoinWalletCard: React.FC<BitcoinWalletBalance> = ({
  availableBalance,
  unconfirmedBalance
}) => {
  return (
    <WalletCard standardSize={4}>
      <span className="text-xl font-bold">Bitcoin Wallet</span>
      <span className="text-3xl font-extrabold text-purple-700">
        {availableBalance.toLocaleString()} sats
      </span>
      <hr />
      <span className="text-slate-500">
        {unconfirmedBalance.toLocaleString()} sats unconfirmed
      </span>
    </WalletCard>
  )
}

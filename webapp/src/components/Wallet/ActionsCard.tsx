import React from 'react'
import { WalletCard } from './WalletCard'
import { ActionButton } from './ActionButton'
import { api } from 'api'
import { useNotification } from '../Context/NotificationContext'

type Props = {
  lightningWalletBalance: number
  channelsBalance: number
}

export const ActionsCard: React.FC<Props> = ({
  lightningWalletBalance,
  channelsBalance
}) => {
  const notify = useNotification()

  const closeChannels = (withForce = false) => {
    api.wallet.closeChannels(withForce).then(({ status }) => {
      if (status === 200) {
        notify('Closing channels...', 'success')
      } else {
        notify('Error while closing channels.', 'error')
      }
    })
  }

  const transfer = () => {
    api.wallet.transfer().then(({ status }) => {
      if (status === 200) {
        notify('Transferring funds to BTC wallet...', 'success')
      } else {
        notify('Error while transferring funds.', 'error')
      }
    })
  }

  return (
    <WalletCard standardSize={3}>
      <ActionButton
        text="Close Channels"
        action={() => closeChannels()}
        disabled={channelsBalance === 0}
      />
      <ActionButton
        text="Force Close Channels"
        action={() => closeChannels(true)}
        modalMessage="Are you sure you want to close with force? It will lock you funds for around a week."
        disabled={channelsBalance === 0}
      />
      <ActionButton
        text="Transfer Between Wallets"
        action={() => transfer()}
        disabled={lightningWalletBalance === 0}
      />
    </WalletCard>
  )
}

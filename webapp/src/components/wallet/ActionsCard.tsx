import React from 'react'
import { WalletCard } from './WalletCard'
import { ActionButton } from './ActionButton'
import { api } from 'api'
import { useNotification } from '../Context/NotificationContext'
import { useTranslation } from 'react-i18next'

type Props = {
  lightningWalletBalance: number
  channelsBalance: number
}

export const ActionsCard: React.FC<Props> = ({
  lightningWalletBalance,
  channelsBalance
}) => {
  const { t } = useTranslation('common')
  const notify = useNotification()

  const closeChannels = (withForce = false) => {
    api.wallet.closeChannels(withForce).then(({ status }) => {
      if (status === 200) {
        notify(t('wallet.actions.closeChannels.success'), 'success')
      } else {
        notify(t('wallet.actions.closeChannels.failure'), 'error')
      }
    })
  }

  const transfer = () => {
    api.wallet.transfer().then(({ status }) => {
      if (status === 200) {
        notify(t('wallet.actions.transfer.success'), 'success')
      } else {
        notify(t('wallet.actions.transfer.failure'), 'error')
      }
    })
  }

  return (
    <WalletCard standardSize={3}>
      <ActionButton
        text={t('wallet.actions.closeChannels.text')}
        action={() => closeChannels()}
        disabled={channelsBalance === 0}
      />
      <ActionButton
        text={t('wallet.actions.closeChannels.textWithForce')}
        action={() => closeChannels(true)}
        modalMessage={t('wallet.actions.closeChannels.forceConfirmationText')}
        disabled={channelsBalance === 0}
      />
      <ActionButton
        text={t('wallet.actions.transfer.text')}
        action={() => transfer()}
        disabled={lightningWalletBalance === 0}
      />
    </WalletCard>
  )
}

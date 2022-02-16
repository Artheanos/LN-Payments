import React, { useState } from 'react'
import { Button } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { ConfirmationModal } from 'components/Modals/ConfirmationModal'
import { StageProps } from 'components/QuickBuy/StageProps'
import { millisecondsToClock, useCountdown } from 'utils/time'

export const TransactionStage: React.FC<StageProps> = ({
  onNext,
  onPrevious,
  setStageIndex,
  payment,
  setPayment
}) => {
  const { t } = useTranslation('common')
  const timeLeft = useCountdown(payment?.expirationTimestamp, () => {
    setModalVisible(true)
    setPayment(null)
  })

  const onCancel = () => {
    setPayment(null)
    onPrevious()
  }

  const [modalVisible, setModalVisible] = useState(false)

  return (
    <div>
      <ConfirmationModal
        confirmButtonContent="Go to setup"
        message={t('quickBuy.transaction.timeoutMessage')}
        onConfirm={() => setStageIndex?.(0)}
        open={modalVisible}
        setOpen={setModalVisible}
      />
      <div>{millisecondsToClock(timeLeft)}</div>
      <Button variant="contained" onClick={onCancel}>
        {t('cancel')}
      </Button>
      <Button variant="contained" color="info" onClick={onNext}>
        {t('next')}
      </Button>
    </div>
  )
}

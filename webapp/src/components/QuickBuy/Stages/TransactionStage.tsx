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
  setPayment
}) => {
  const { t } = useTranslation('common')
  const timeLeft = useCountdown(5_000, () => {
    setModalVisible(true)
    setPayment(null)
  })

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
      <Button variant="contained" onClick={onPrevious}>
        {t('cancel')}
      </Button>
      <Button variant="contained" color="info" onClick={onNext}>
        {t('next')}
      </Button>
    </div>
  )
}

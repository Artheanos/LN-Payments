import React, { useEffect, useMemo, useState } from 'react'
import { Button } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { ConfirmationModal } from '../../Modals/ConfirmationModal'
import { StageProps } from '../StageProps'
import { millisecondsToClock, secondsFromNow } from 'utils/dates'

export const TransactionStage: React.FC<StageProps> = ({
  onNext,
  onPrevious,
  setStageIndex
}) => {
  const { t } = useTranslation('common')
  const deadline = useMemo(() => secondsFromNow(5), [])
  const [timeLeft, setTimeLeft] = useState(
    deadline.valueOf() - new Date().valueOf() - 1
  )

  const [modalVisible, setModalVisible] = useState(false)

  useEffect(() => {
    if (timeLeft > 0) {
      const interval = setTimeout(() => {
        let newTimeLeft = deadline.valueOf() - new Date().valueOf()
        if (newTimeLeft < 0) {
          newTimeLeft = 0
        }
        setTimeLeft(newTimeLeft)
      }, 1000)
      return () => clearTimeout(interval)
    } else {
      setModalVisible(true)
    }
  }, [deadline, t, timeLeft])

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

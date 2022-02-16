import React, { useEffect, useMemo, useState } from 'react'

import { SetupStage } from './Stages/SetupStage'
import { StageProgress } from './StageProgress/StageProgress'
import { TokensStage } from './Stages/TokensStage'
import { TransactionStage } from './Stages/TransactionStage'
import { useLocalStorage } from 'utils/persist'

const STAGE_COMPONENTS = [SetupStage, TransactionStage, TokensStage]
enum StageIndex {
  Setup,
  Transaction,
  Tokens
}

const zeroIfNotInRange = (index: number) => {
  const isInRange = index >= 0 && index < STAGE_COMPONENTS.length
  return isInRange ? index : 0
}

export const QuickBuy: React.FC = () => {
  const [stageIndex, setStageIndex] = useState<StageIndex>(StageIndex.Setup)
  const [payment, setPayment] = useLocalStorage<PaymentDetails | null>(
    'paymentDetails'
  )

  useEffect(() => {
    if (payment) {
      setStageIndex(StageIndex.Transaction)
    }
  }, [payment])

  const CurrentStage = useMemo(() => STAGE_COMPONENTS[stageIndex], [stageIndex])

  return (
    <div className="w-auto text-center">
      <div className="mt-10">
        <StageProgress currentStageIndex={stageIndex} />
      </div>
      <div className="pt-20">
        <CurrentStage
          onNext={() => setStageIndex((prev) => zeroIfNotInRange(prev + 1))}
          onPrevious={() => setStageIndex((prev) => zeroIfNotInRange(prev - 1))}
          {...{ payment, setPayment, setStageIndex }}
        />
      </div>
    </div>
  )
}

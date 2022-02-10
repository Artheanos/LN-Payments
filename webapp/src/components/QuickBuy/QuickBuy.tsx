import React, { useMemo, useState } from 'react'

import { SetupStage } from './Stages/SetupStage'
import { StageProgress } from './StageProgress/StageProgress'
import { TokensStage } from './Stages/TokensStage'
import { TransactionStage } from './Stages/TransactionStage'

const stageComponents = [SetupStage, TransactionStage, TokensStage]

const zeroIfNotInRange = (index: number) => {
  const isInRange = index >= 0 && index < stageComponents.length
  return isInRange ? index : 0
}

export const QuickBuy: React.FC = () => {
  const [stageIndex, setStageIndex] = useState(0)

  const currentStage = useMemo(() => {
    const StageComponent = stageComponents[stageIndex]
    return (
      <StageComponent
        onNext={() => setStageIndex((prev) => zeroIfNotInRange(prev + 1))}
        onPrevious={() => setStageIndex((prev) => zeroIfNotInRange(prev - 1))}
        setStageIndex={setStageIndex}
      />
    )
  }, [stageIndex])

  return (
    <div className="w-auto text-center">
      <div className="mt-10">
        <StageProgress currentStageIndex={stageIndex} />
      </div>
      <div className="pt-20">{currentStage}</div>
    </div>
  )
}

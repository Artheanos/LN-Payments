import React, { useContext, useEffect, useMemo, useState } from 'react'

import { LocalKey } from '@constants'
import { SetupStage } from './Stages/SetupStage'
import { StageProgress } from './StageProgress/StageProgress'
import { TokensStage } from './Stages/TokensStage'
import { TransactionStage } from './Stages/TransactionStage/TransactionStage'
import { UserContext } from 'components/Context/UserContext'
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
  const [payment, setPayment] = useLocalStorage<PaymentDetails>(
    LocalKey.PAYMENT
  )
  const [tokens] = useLocalStorage<string[]>(LocalKey.TRANSACTION_TOKENS)
  const { isValid } = useContext(UserContext)

  useEffect(() => {
    let redirectTo: StageIndex | undefined = undefined

    if (tokens) {
      redirectTo = StageIndex.Tokens
    } else if (payment) {
      redirectTo = StageIndex.Transaction
    } else if (!isValid) {
      redirectTo = StageIndex.Setup
    }

    if (redirectTo) setStageIndex(redirectTo)
  }, [tokens, payment, isValid])

  const CurrentStage = useMemo(() => STAGE_COMPONENTS[stageIndex], [stageIndex])

  return (
    <div className="w-full text-center">
      <div className="mt-10">
        <StageProgress currentStageIndex={stageIndex} />
      </div>
      <div className="px-10 pt-10">
        <CurrentStage
          onNext={() => setStageIndex((prev) => zeroIfNotInRange(prev + 1))}
          onPrevious={() => setStageIndex((prev) => zeroIfNotInRange(prev - 1))}
          {...{ payment, setPayment, setStageIndex }}
        />
      </div>
    </div>
  )
}

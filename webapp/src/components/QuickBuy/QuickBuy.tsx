import React, { useContext, useEffect, useMemo, useState } from 'react'
import { CircularProgress } from '@mui/material'

import { LocalKey } from 'constants/LocalKey'
import { PaymentDetails } from 'common-ts/dist/webServiceApi/interface/payment'
import { SetupStage } from './Stages/SetupStage'
import { StageProgress } from './StageProgress/StageProgress'
import { TokensStage } from './Stages/TokensStage/TokensStage'
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
  const [stageIndex, setStageIndex] = useState<StageIndex | undefined>(
    undefined
  )
  const [payment, setPayment] = useLocalStorage<PaymentDetails>(
    LocalKey.PAYMENT
  )
  const [tokens, setTokens] = useLocalStorage<string[]>(
    LocalKey.TRANSACTION_TOKENS
  )
  const { isLoggedIn } = useContext(UserContext)

  useEffect(() => {
    let redirectTo: StageIndex | undefined = undefined

    if (tokens) {
      redirectTo = StageIndex.Tokens
    } else if (payment) {
      redirectTo = StageIndex.Transaction
    } else if (isLoggedIn) {
      redirectTo = StageIndex.Setup
    }

    setStageIndex(redirectTo)
  }, [tokens, payment, isLoggedIn])

  const CurrentStage = useMemo(
    () => (stageIndex !== undefined ? STAGE_COMPONENTS[stageIndex] : undefined),
    [stageIndex]
  )

  if (stageIndex === undefined || !CurrentStage) {
    return <CircularProgress />
  }

  return (
    <div className="w-full text-center">
      <div className="mt-2">
        <StageProgress currentStageIndex={stageIndex} />
      </div>
      <div className="px-10 pt-10">
        <CurrentStage
          onNext={() => setStageIndex((prev) => zeroIfNotInRange(prev! + 1))}
          onPrevious={() =>
            setStageIndex((prev) => zeroIfNotInRange(prev! - 1))
          }
          {...{ payment, setPayment, setStageIndex, tokens, setTokens }}
        />
      </div>
    </div>
  )
}

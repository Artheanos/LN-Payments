import React, { useContext, useEffect, useMemo, useState } from 'react'
import { CircularProgress } from '@mui/material'

import { LocalKey } from 'constants/LocalKey'
import { PaymentDetails, PaymentInfo } from 'webService/interface/payment'
import { SetupStage } from './Stages/SetupStage'
import { StageProgress } from './StageProgress'
import { TokensStage } from './Stages/TokensStage'
import { TransactionStage } from './Stages/TransactionStage'
import { UserContext } from 'components/Context/UserContext'
import { useLocalStorage } from 'utils/persist'
import { api } from 'webService/requests'

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
  const [paymentInfo, setPaymentInfo] = useState<PaymentInfo>()
  const [payment, setPayment] = useLocalStorage<PaymentDetails>(
    LocalKey.PAYMENT
  )
  const [tokens, setTokens] = useLocalStorage<string[]>(
    LocalKey.TRANSACTION_TOKENS
  )
  const { isLoggedIn } = useContext(UserContext)

  useEffect(() => {
    let redirectTo: StageIndex | undefined

    if (tokens) {
      redirectTo = StageIndex.Tokens
    } else if (payment) {
      redirectTo = StageIndex.Transaction
    } else {
      redirectTo = StageIndex.Setup
    }

    setStageIndex(redirectTo)
  }, [tokens, payment, isLoggedIn])

  useEffect(() => {
    api.payment.info().then(({ data }) => setPaymentInfo(data))
  }, [])

  const CurrentStage = useMemo(
    () => (stageIndex !== undefined ? STAGE_COMPONENTS[stageIndex] : undefined),
    [stageIndex]
  )

  if (stageIndex === undefined || !CurrentStage || !paymentInfo) {
    return (
      <div className="pt-10 w-full text-center">
        <CircularProgress />
      </div>
    )
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
          {...{
            payment,
            setPayment,
            paymentInfo,
            setStageIndex,
            tokens,
            setTokens
          }}
        />
      </div>
    </div>
  )
}

import React from 'react'

import { StageHeader } from './StageHeader'
import { useTranslation } from 'react-i18next'

interface Props {
  currentStageIndex: number
}

export const StageProgress: React.FC<Props> = ({ currentStageIndex }) => {
  const { t } = useTranslation('quickBuy')
  const stages = [
    t('setup.header'),
    t('transaction.header'),
    t('tokens.header')
  ]

  return (
    <div className="grid grid-flow-col text-4xl font-extrabold text-center">
      {stages.map((stageName, index) => (
        <StageHeader
          currentStageIndex={currentStageIndex}
          stageName={stageName}
          stageIndex={index}
          key={index}
        />
      ))}
    </div>
  )
}

import React from 'react'
import { StageHeader } from './StageHeader'

interface Props {
  currentStageIndex: number
}

const stages = ['Setup', 'Transaction', 'Tokens']

export const StageProgress: React.FC<Props> = ({ currentStageIndex }) => {
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

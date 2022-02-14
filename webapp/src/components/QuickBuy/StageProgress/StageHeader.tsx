import React from 'react'

interface Props {
  currentStageIndex: number
  stageIndex: number
  stageName: string
}

export enum HeaderClassName {
  PAST = 'text-gray-500',
  PRESENT = 'text-purple-500',
  FUTURE = ''
}

export const StageHeader: React.FC<Props> = ({
  currentStageIndex,
  stageIndex,
  stageName
}) => {
  let className = HeaderClassName.PAST
  if (stageIndex === currentStageIndex) {
    className = HeaderClassName.PRESENT
  } else if (stageIndex > currentStageIndex) {
    className = HeaderClassName.FUTURE
  }

  return (
    <div className={className} key={stageIndex}>
      {stageName}
    </div>
  )
}

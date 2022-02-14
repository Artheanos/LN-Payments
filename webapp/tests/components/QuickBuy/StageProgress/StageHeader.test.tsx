import React from 'react'

import { render, screen } from 'tests/test-utils'
import {
  HeaderClassName,
  StageHeader
} from 'components/QuickBuy/StageProgress/StageHeader'

describe('StageProgress', () => {
  const stageIndex = 5

  it.each([
    { currentStageIndex: 4, expectedClassName: HeaderClassName.FUTURE },
    { currentStageIndex: 5, expectedClassName: HeaderClassName.PRESENT },
    { currentStageIndex: 6, expectedClassName: HeaderClassName.PAST }
  ])(
    'uses proper class name according to current stage index',
    ({ currentStageIndex, expectedClassName }) => {
      render(
        <StageHeader
          stageName={'STAGE NAME'}
          currentStageIndex={currentStageIndex}
          stageIndex={stageIndex}
        />
      )

      expect(screen.getByText('STAGE NAME')).toBeInTheDocument()
      expect(screen.getByText('STAGE NAME').className).toBe(expectedClassName)
    }
  )
})

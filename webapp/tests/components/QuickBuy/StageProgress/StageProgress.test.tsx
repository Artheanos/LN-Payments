import React from 'react'

import { HeaderClassName } from 'components/QuickBuy/StageProgress/StageHeader'
import { StageProgress } from 'components/QuickBuy/StageProgress/StageProgress'
import { render, screen } from 'tests/test-utils'

describe('StageProgress', () => {
  const cases = [
    {
      currentStageIndex: 0,
      expectedClassNames: {
        Setup: HeaderClassName.PRESENT,
        Transaction: HeaderClassName.FUTURE,
        Tokens: HeaderClassName.FUTURE
      }
    },
    {
      currentStageIndex: 1,
      expectedClassNames: {
        Setup: HeaderClassName.PAST,
        Transaction: HeaderClassName.PRESENT,
        Tokens: HeaderClassName.FUTURE
      }
    },
    {
      currentStageIndex: 2,
      expectedClassNames: {
        Setup: HeaderClassName.PAST,
        Transaction: HeaderClassName.PAST,
        Tokens: HeaderClassName.PRESENT
      }
    }
  ]

  it.each(cases)(
    'renders proper class names',
    ({ currentStageIndex, expectedClassNames }) => {
      render(<StageProgress currentStageIndex={currentStageIndex} />)

      Object.entries(expectedClassNames).forEach(([stageName, className]) => {
        expect(screen.getByText(stageName).className).toBe(className)
      })
    }
  )
})

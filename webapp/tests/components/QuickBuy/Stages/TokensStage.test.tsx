import React from 'react'
import { render, screen } from 'tests/test-utils'

import { TokensStage } from 'components/QuickBuy/Stages/TokensStage'

describe('TokensStage', () => {
  it('calls onNext when pressing Close', () => {
    const onNext = jest.fn(() => {})
    const onPrevious = jest.fn(() => {})
    render(<TokensStage onNext={onNext} onPrevious={onPrevious} />)

    expect(onNext.mock.calls.length).toBe(0)
    screen.getByText('Close').click()
    expect(onNext.mock.calls.length).toBe(1)
  })
})

import React from 'react'
import { render, screen } from 'tests/test-utils'

import { TokensStage } from 'components/QuickBuy/Stages/TokensStage/TokensStage'

describe('TokensStage', () => {
  it('calls onNext when pressing Close', () => {
    const onNext = jest.fn(() => {})
    const onPrevious = jest.fn(() => {})
    const setPayment = jest.fn(() => {})
    const setTokens = jest.fn(() => {})
    const tokens = ['token']

    render(
      <TokensStage {...{ onNext, onPrevious, setPayment, tokens, setTokens }} />
    )

    expect(onNext.mock.calls.length).toBe(0)
    screen.getByText('Close').click()
    expect(onNext.mock.calls.length).toBe(1)
  })
})

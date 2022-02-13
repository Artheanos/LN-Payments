import React from 'react'
import { render, screen } from 'tests/test-utils'
import { SetupStage } from 'components/QuickBuy/Stages/SetupStage'

describe('SetupStage', () => {
  let onPrevious: jest.Mock
  let onNext: jest.Mock

  beforeEach(() => {
    onPrevious = jest.fn(() => {})
    onNext = jest.fn(() => {})
    render(<SetupStage onPrevious={onPrevious} onNext={onNext} />)
  })

  it('renders info', () => {
    expect(screen.getByText('Tokens are very cool things')).toBeInTheDocument()
  })

  it('calls on next when pressing Next', () => {
    expect(onPrevious.mock.calls.length).toBe(0)
    expect(onNext.mock.calls.length).toBe(0)

    screen.getByText('Next').click()

    expect(onPrevious.mock.calls.length).toBe(0)
    expect(onNext.mock.calls.length).toBe(1)
  })
})

import React from 'react'
import { render, screen, waitFor } from 'tests/test-utils'
import { TransactionStage } from 'components/QuickBuy/Stages/TransactionStage'

jest.mock('utils/dates', () => {
  const originalModule = jest.requireActual('utils/dates')
  return {
    ...originalModule,
    secondsFromNow: () => originalModule.secondsFromNow(2)
  }
})

describe('TransactionStage', () => {
  let onPrevious: jest.Mock
  let onNext: jest.Mock
  let setStageIndex: jest.Mock

  beforeEach(() => {
    onPrevious = jest.fn(() => {})
    onNext = jest.fn(() => {})
    setStageIndex = jest.fn(() => {})
    render(
      <TransactionStage
        onPrevious={onPrevious}
        onNext={onNext}
        setStageIndex={setStageIndex}
      />
    )
  })

  it('renders time left', async () => {
    expect(screen.getByText('00:01')).toBeInTheDocument()
    await waitFor(() => screen.getByText('00:00'))
  })

  describe('timeout', () => {
    beforeEach(async () => {
      await waitFor(() => screen.getByText('Go to setup'), { timeout: 2100 })
    })

    it('shows confirmation modal', () => {
      expect(screen.getByText('Transaction has expired')).toBeInTheDocument()
    })

    it('calls setStageIndex when confirming modal', () => {
      screen.getByText('Go to setup').click()
      expect(setStageIndex.mock.calls.length).toBe(1)
    })
  })

  it('calls onPrevious when pressing Cancel', () => {
    expect(onPrevious.mock.calls.length).toBe(0)
    expect(onNext.mock.calls.length).toBe(0)

    screen.getByText('Cancel').click()

    expect(onPrevious.mock.calls.length).toBe(1)
    expect(onNext.mock.calls.length).toBe(0)
  })
})

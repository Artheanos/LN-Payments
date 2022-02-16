import React from 'react'
import { render, screen, waitFor } from 'tests/test-utils'
import { TransactionStage } from 'components/QuickBuy/Stages/TransactionStage'

describe('TransactionStage', () => {
  const fakePaymentDetails = {
    paymentRequest: '123',
    timestamp: new Date(),
    expirationTimestamp: new Date(new Date().valueOf() + 4_000)
  }

  let onPrevious: jest.Mock
  let onNext: jest.Mock
  let setStageIndex: jest.Mock
  let setPayment: jest.Mock

  beforeEach(() => {
    onPrevious = jest.fn(() => {})
    onNext = jest.fn(() => {})
    setStageIndex = jest.fn(() => {})
    setPayment = jest.fn(() => {})

    render(
      <TransactionStage
        payment={fakePaymentDetails}
        {...{ onNext, onPrevious, setStageIndex, setPayment }}
      />
    )
  })

  it('renders time left', async () => {
    expect(screen.getByText('00:03')).toBeInTheDocument()
    await waitFor(() => screen.getByText('00:02'))
  })

  describe('timeout', () => {
    beforeEach(async () => {
      await waitFor(() => screen.getByText('Go to setup'), { timeout: 5000 })
    })

    it('shows confirmation modal', () => {
      expect(screen.getByText('Transaction has expired')).toBeInTheDocument()
    })

    it('calls setStageIndex when confirming modal', () => {
      screen.getByText('Go to setup').click()
      expect(setStageIndex).toHaveBeenCalledTimes(1)
    })
  })

  it('calls onPrevious when pressing Cancel', () => {
    expect(onPrevious).not.toHaveBeenCalled()
    expect(onNext).not.toHaveBeenCalled()

    screen.getByText('Cancel').click()

    expect(onPrevious).toHaveBeenCalledTimes(1)
    expect(onNext).not.toHaveBeenCalled()
  })
})

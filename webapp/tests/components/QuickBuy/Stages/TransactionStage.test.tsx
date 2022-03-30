import React from 'react'
import { render, screen, waitFor } from 'tests/test-utils'
import { TransactionStage } from 'components/QuickBuy/Stages/TransactionStage/TransactionStage'
import { setupServer } from 'msw/node'
import { rest } from 'msw'
import routesBuilder from 'routesBuilder'
import { PaymentStatus } from '@constants'

jest.mock('@stomp/stompjs', () => {
  const originalModule = jest.requireActual('@stomp/stompjs')

  return {
    __esModule: true,
    ...originalModule,
    Client: class {
      onConnect = () => {}

      constructor({ onConnect }: { onConnect: () => void }) {
        this.onConnect = onConnect
      }

      subscribe() {}

      activate() {
        this.onConnect()
      }
    }
  }
})

const server = setupServer(
  rest.get(routesBuilder.api.payments.info, (req, res, ctx) => {
    return res(ctx.json({ price: 100 }))
  })
)

describe('TransactionStage', () => {
  const fakePaymentDetails = {
    paymentTopic: '/topic/123',
    paymentRequest: '123',
    timestamp: new Date(),
    expirationTimestamp: new Date(new Date().valueOf() + 4_000),
    price: 1,
    numberOfTokens: 1,
    paymentStatus: PaymentStatus.PENDING,
    tokens: []
  }

  let onPrevious: jest.Mock
  let onNext: jest.Mock
  let setStageIndex: jest.Mock
  let setPayment: jest.Mock

  beforeEach(async () => {
    onPrevious = jest.fn(() => {})
    onNext = jest.fn(() => {})
    setStageIndex = jest.fn(() => {})
    setPayment = jest.fn(() => {})
    const payment = fakePaymentDetails
    server.listen()

    render(
      <TransactionStage
        {...{ onNext, onPrevious, setStageIndex, payment, setPayment }}
      />
    )

    await waitFor(() => screen.getByText('Cancel'))
  })

  afterEach(() => server.close())

  it('renders time left', async () => {
    await waitFor(() => screen.getByText('00:03'))
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

  it('calls onPrevious when pressing Cancel', async () => {
    expect(onPrevious).not.toHaveBeenCalled()
    expect(onNext).not.toHaveBeenCalled()

    screen.getByText('Cancel').click()

    expect(onPrevious).toHaveBeenCalledTimes(1)
    expect(onNext).not.toHaveBeenCalled()
  })
})

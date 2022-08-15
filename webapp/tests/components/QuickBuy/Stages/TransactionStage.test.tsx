import React from 'react'
import { render, screen, waitFor } from 'tests/test-utils'
import { rest } from 'msw'
import { setupServer } from 'msw/node'

import routesBuilder from 'routesBuilder'
import { PaymentDetails, PaymentInfo } from 'webService/interface/payment'
import { TransactionStage } from 'components/QuickBuy/Stages/TransactionStage'
import { paymentInfoMock } from 'tests/mockData/paymentInfoMock'

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

      deactivate() {}
    }
  }
})

const server = setupServer(
  rest.get(routesBuilder.api.payments.info, (req, res, ctx) => {
    const response: Partial<PaymentInfo> = {
      price: 100
    }
    return res(ctx.json(response))
  })
)

describe('TransactionStage', () => {
  const fakePaymentDetails = {
    paymentTopic: '/topic/123',
    paymentRequest: '123',
    expirationTimestamp: new Date(new Date().valueOf() + 4_000)
  } as PaymentDetails

  let onPrevious: jest.Mock
  let onNext: jest.Mock
  let setStageIndex: jest.Mock
  let setPayment: jest.Mock
  let setTokens: jest.Mock

  beforeEach(async () => {
    setTokens = jest.fn(() => {})
    onPrevious = jest.fn(() => {})
    onNext = jest.fn(() => {})
    setStageIndex = jest.fn(() => {})
    setPayment = jest.fn(() => {})
    const payment = { ...fakePaymentDetails, numberOfTokens: 2 }
    server.listen()

    render(
      <TransactionStage
        {...{
          onNext,
          onPrevious,
          setStageIndex,
          payment,
          paymentInfo: paymentInfoMock,
          setPayment,
          setTokens
        }}
      />
    )

    await waitFor(() => screen.getByText('Cancel'))
  })

  afterEach(() => server.close())

  it('renders time left', async () => {
    await waitFor(() => screen.getByText('00:03'))
    await waitFor(() => screen.getByText('00:02'))
  })

  it('calls onPrevious when pressing Cancel', async () => {
    expect(onPrevious).not.toHaveBeenCalled()
    expect(onNext).not.toHaveBeenCalled()

    screen.getByText('Cancel').click()

    expect(onPrevious).toHaveBeenCalledTimes(1)
    expect(onNext).not.toHaveBeenCalled()
  })

  it('displays proper price', () => {
    expect(screen.getByText('200')).toBeInTheDocument()
    expect(screen.getByText('SAT')).toBeInTheDocument()
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
})

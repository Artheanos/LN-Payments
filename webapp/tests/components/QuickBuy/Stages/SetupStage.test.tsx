import React from 'react'
import { rest } from 'msw'
import { setupServer } from 'msw/node'

import routesBuilder from 'routesBuilder'
import { SetupStage } from 'components/QuickBuy/Stages/SetupStage'
import { render, screen, waitFor } from 'tests/test-utils'

describe('SetupStage', () => {
  const fakePaymentDetails = {
    paymentRequest: '123',
    timestamp: new Date(),
    expirationTimestamp: new Date()
  }

  const server = setupServer(
    rest.post(routesBuilder.api.payments.index, (req, res, ctx) => {
      return res(ctx.json(fakePaymentDetails))
    })
  )

  beforeAll(() => server.listen())
  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  let onPrevious: jest.Mock
  let onNext: jest.Mock
  let setPayment: jest.Mock

  beforeEach(() => {
    onPrevious = jest.fn(() => {})
    onNext = jest.fn(() => {})
    setPayment = jest.fn(() => {})
    render(
      <SetupStage
        onPrevious={onPrevious}
        onNext={onNext}
        setPayment={setPayment}
      />
    )
  })

  it('renders info', () => {
    expect(
      screen.getByText('Tokens are very cool things', { exact: false })
    ).toBeInTheDocument()
  })

  it('calls setPayment when pressing Next', async () => {
    expect(onPrevious).not.toHaveBeenCalled()
    expect(setPayment).not.toHaveBeenCalled()
    expect(onNext).not.toHaveBeenCalled()

    screen.getByText('Next').click()
    await waitFor(() => expect(onNext).toHaveBeenCalled())

    expect(onPrevious).not.toHaveBeenCalled()
    expect(setPayment).toHaveBeenCalledWith(fakePaymentDetails)
    expect(onNext).toHaveBeenCalledTimes(1)
  })
})

import React from 'react'
import { rest } from 'msw'
import { setupServer } from 'msw/node'

import routesBuilder from 'routesBuilder'
import { Role } from 'webService/interface/user'
import { SetupStage } from 'components/QuickBuy/Stages/SetupStage'
import { paymentInfoMock } from 'tests/mockData/paymentInfoMock'
import { render, screen, waitFor, fireEvent } from 'tests/test-utils'

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

  let role: Role

  beforeAll(() => server.listen())
  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  let onPrevious: jest.Mock
  let onNext: jest.Mock
  let setPayment: jest.Mock
  let setTokens: jest.Mock

  beforeEach(() => {
    onPrevious = jest.fn(() => {})
    onNext = jest.fn(() => {})
    setPayment = jest.fn(() => {})
    setTokens = jest.fn(() => {})
    render(
      <SetupStage
        {...{
          onPrevious,
          onNext,
          setTokens,
          setPayment,
          paymentInfo: paymentInfoMock
        }}
      />,
      {
        role,
        isLoggedIn: true
      }
    )
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

  describe('when user has temporary role', () => {
    beforeAll(() => {
      role = Role.TEMPORARY
    })

    it('renders correct info', () => {
      expect(
        screen.getByText(
          'Purchased tokens will be shown at the last screen. Make sure to save them before closing or else they will be gone forever.'
        )
      ).toBeInTheDocument()
    })

    it('calls setPayment when data is valid', async () => {
      expect(onPrevious).not.toHaveBeenCalled()
      expect(setPayment).not.toHaveBeenCalled()
      expect(onNext).not.toHaveBeenCalled()
      fireEvent.change(screen.getByLabelText('Email'), {
        target: { value: 'admin@admin.pl' }
      })
      screen.getByText('Next').click()
      await waitFor(() => {
        expect(onNext).toHaveBeenCalled()
      })

      expect(onPrevious).not.toHaveBeenCalled()
      expect(setPayment).toHaveBeenCalledWith(fakePaymentDetails)
      expect(onNext).toHaveBeenCalledTimes(1)
    })

    it('does not call setPayment and shows error when form is invalid', async () => {
      fireEvent.change(screen.getByLabelText('Email'), {
        target: { value: 'admin' }
      })
      fireEvent.change(screen.getByLabelText('Number of tokens'), {
        target: { value: '-1' }
      })

      await waitFor(() => {
        screen.getByText('Next').click()
      })

      expect(onPrevious).not.toHaveBeenCalled()
      expect(
        screen.queryByText(
          'Number of tokens must be greater than or equal to 1'
        )
      ).toBeInTheDocument()
      expect(
        screen.queryByText('Email must be a valid email')
      ).toBeInTheDocument()
    })
  })

  describe('when user has admin role', () => {
    beforeAll(() => {
      role = Role.ADMIN
    })

    it('renders correct info', () => {
      expect(
        screen.getByText(
          'After purchase tokens will be saved to your account. You can view them anytime.'
        )
      ).toBeInTheDocument()
    })

    it('does not show or require email', async () => {
      screen.getByText('Next').click()
      await waitFor(() => {
        expect(screen.queryByText('Email')).not.toBeInTheDocument()
        expect(onNext).toHaveBeenCalled()
      })

      expect(onPrevious).not.toHaveBeenCalled()
      expect(setPayment).toHaveBeenCalledWith(fakePaymentDetails)
      expect(onNext).toHaveBeenCalledTimes(1)
    })
  })
})

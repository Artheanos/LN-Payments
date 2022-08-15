import React from 'react'
import { render, screen } from 'tests/test-utils'

import { TokensStage } from 'components/QuickBuy/Stages/TokensStage'
import { Role } from 'webService/interface/user'
import { paymentInfoMock } from 'tests/mockData/paymentInfoMock'

describe('TokensStage', () => {
  let onNext: jest.Mock
  let role: Role

  beforeEach(() => {
    onNext = jest.fn(() => {})
    const onPrevious = jest.fn(() => {})
    const setPayment = jest.fn(() => {})
    const setTokens = jest.fn(() => {})
    const tokens = ['token']

    render(
      <TokensStage
        {...{
          onNext,
          onPrevious,
          setPayment,
          tokens,
          setTokens,
          paymentInfo: paymentInfoMock
        }}
      />,
      {
        role
      }
    )
  })

  describe('when user is admin', () => {
    beforeAll(() => {
      role = Role.ADMIN
    })

    it('calls onNext when pressing Close', () => {
      expect(onNext.mock.calls.length).toBe(0)
      screen.getByText('Close').click()
      expect(onNext.mock.calls.length).toBe(1)
    })

    it('does not show warning', () => {
      expect(
        screen.queryByText(
          'After closing this stage the tokens will be gone forever'
        )
      ).not.toBeInTheDocument()
    })
  })

  describe('when user is temporary', () => {
    beforeAll(() => {
      role = Role.TEMPORARY
    })

    it('shows warning', () => {
      expect(
        screen.getByText(
          'After closing this stage the tokens will be gone forever'
        )
      ).toBeInTheDocument()
    })
  })
})

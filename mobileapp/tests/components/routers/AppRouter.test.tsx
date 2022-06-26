import React from 'react'
import { render, waitFor, screen } from '@testing-library/react-native'

import { AppRouter } from 'components/routers/AppRouter'
import { HelperComponent } from 'tests/concern/HelperComponent'
import { SignedInRouter } from 'components/routers/SignedInRouter'
import { SignedOutRouter } from 'components/routers/SignedOutRouter'
import { UserContext } from 'components/context/UserContext'

const RouterMock = <></>

jest.mock('@notifee/react-native', () => ({
  onForegroundEvent: jest.fn(),
  onBackgroundEvent: jest.fn(),
}))

jest.mock('components/routers/SignedOutRouter', () => ({
  SignedOutRouter: jest.fn(),
}))

jest.mock('components/routers/SignedInRouter', () => ({
  SignedInRouter: jest.fn(),
}))

describe('AppRouter', () => {
  let userContextValue: Record<string, unknown>

  beforeEach(() => {
    ;(SignedOutRouter as jest.Mock).mockClear().mockReturnValue(RouterMock)
    ;(SignedInRouter as jest.Mock).mockClear().mockReturnValue(RouterMock)
    render(
      <HelperComponent>
        <UserContext.Provider value={userContextValue as never}>
          <AppRouter />
        </UserContext.Provider>
      </HelperComponent>,
    )
  })

  describe('when user has both token and a privateKey', () => {
    beforeAll(() => {
      userContextValue = {
        user: {
          token: 'TOKEN',
          privateKey: '123',
        },
        userLoading: false,
      }
    })

    it('shows the signed in router', async () => {
      await waitFor(() => {
        expect(screen.queryByLabelText('loading')).toBeFalsy()
        expect(SignedOutRouter).not.toHaveBeenCalled()

        expect(SignedInRouter).toHaveBeenCalled()
      })
    })

    describe('when user data is loading', () => {
      beforeAll(() => {
        userContextValue.userLoading = true
      })

      it('shows a spinner', async () => {
        await waitFor(() => {
          expect(screen.queryByLabelText('loading')).toBeTruthy()
        })
      })
    })
  })

  describe('when user does not have a privateKey', () => {
    beforeAll(() => {
      userContextValue = {
        user: {
          token: 'TOKEN',
          privateKey: null,
        },
        userLoading: false,
      }
    })

    it('shows the signed out router', async () => {
      await waitFor(() => {
        expect(screen.queryByLabelText('loading')).toBeFalsy()
        expect(SignedInRouter).not.toHaveBeenCalled()

        expect(SignedOutRouter).toHaveBeenCalled()
      })
    })
  })
})

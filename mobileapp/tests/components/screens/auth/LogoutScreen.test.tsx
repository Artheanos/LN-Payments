import React from 'react'
import { render, waitFor } from '@testing-library/react-native'

import { HelperComponent } from 'tests/concern/HelperComponent'
import { LogoutScreen } from 'components/screens/auth/LogoutScreen'
import { UserContext } from 'components/context/UserContext'

describe('LogoutScreen', () => {
  it('logs out', async () => {
    const logoutUserMock = jest.fn()
    const { getByText } = render(
      <HelperComponent>
        <UserContext.Provider value={{ logoutUser: logoutUserMock } as never}>
          <LogoutScreen />
        </UserContext.Provider>
      </HelperComponent>,
    )

    await waitFor(() => {
      expect(logoutUserMock).toHaveBeenCalled()
      expect(getByText('Logging out...')).toBeDefined()
    })
  })
})

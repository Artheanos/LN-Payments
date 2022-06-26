import React from 'react'
import { render, screen } from '@testing-library/react-native'
import { HelperComponent } from 'tests/concern/HelperComponent'
import { SignedOutRouter } from 'components/routers/SignedOutRouter'
import { UserContext } from 'components/context/UserContext'

describe('SignedOutRouter', () => {
  it('renders key upload screen', () => {
    render(
      <HelperComponent>
        <UserContext.Provider value={{ user: { uploadKeys: true } } as never}>
          <SignedOutRouter />
        </UserContext.Provider>
      </HelperComponent>,
    )

    expect(screen.queryByText('Uploading keys')).toBeTruthy()
    expect(screen.queryByText('Login')).toBeFalsy()
  })

  it('renders login screen', () => {
    render(
      <HelperComponent>
        <UserContext.Provider value={{ user: { uploadKeys: false } } as never}>
          <SignedOutRouter />
        </UserContext.Provider>
      </HelperComponent>,
    )

    expect(screen.queryByText('Uploading keys')).toBeFalsy()
    expect(screen.queryAllByText('Login')).toBeTruthy()
  })
})

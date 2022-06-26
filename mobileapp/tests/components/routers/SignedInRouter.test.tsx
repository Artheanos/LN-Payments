import React from 'react'
import { render } from '@testing-library/react-native'
import { HelperComponent } from 'tests/concern/HelperComponent'
import { SignedInRouter } from 'components/routers/SignedInRouter'
import { UserContext } from 'components/context/UserContext'
import { DrawerRouter } from 'components/routers/DrawerRouter'
import { NotificationDetailScreen } from 'components/screens/notification/NotificationDetailScreen/NotificationDetailScreen'

jest.mock(
  'components/screens/notification/NotificationDetailScreen/NotificationDetailScreen',
  () => ({
    NotificationDetailScreen: jest.fn(),
  }),
)

jest.mock('components/routers/DrawerRouter', () => ({
  DrawerRouter: jest.fn(),
}))

describe('SignedInRouter', () => {
  it('renders the first component listed in the stack', () => {
    ;(DrawerRouter as jest.Mock).mockReturnValue(<></>)
    ;(NotificationDetailScreen as jest.Mock).mockReturnValue(<></>)
    render(
      <HelperComponent>
        <UserContext.Provider value={{ user: { uploadKeys: true } } as never}>
          <SignedInRouter />
        </UserContext.Provider>
      </HelperComponent>,
    )

    expect(DrawerRouter).toHaveBeenCalled()
    expect(NotificationDetailScreen).not.toHaveBeenCalled()
  })
})

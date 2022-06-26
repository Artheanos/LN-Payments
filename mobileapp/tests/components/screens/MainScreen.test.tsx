import React from 'react'
import { render } from '@testing-library/react-native'

import { HelperComponent } from 'tests/concern/HelperComponent'
import { MainScreen } from 'components/screens/MainScreen'
import { NotificationListScreen } from 'components/screens/notification/NotificationListScreen/NotificationListScreen'

jest.mock(
  'components/screens/notification/NotificationListScreen/NotificationListScreen',
  () => ({
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    NotificationListScreen: jest.fn(function NotificationListScreenMock(
      ..._args: unknown[]
    ) {
      return <></>
    }),
  }),
)

describe('MainScreen', () => {
  it('calls NotificationListScreen', () => {
    render(
      <HelperComponent>
        <MainScreen navigation={null as never} />
      </HelperComponent>,
    )
    expect((NotificationListScreen as jest.Mock).mock.calls.length).toEqual(1)
  })
})

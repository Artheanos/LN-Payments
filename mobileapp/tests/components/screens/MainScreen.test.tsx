import React from 'react'

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer'

import { MainScreen } from 'components/screens/MainScreen'
import { NotificationListScreen } from 'components/screens/notification/NotificationListScreen/NotificationListScreen'
import { HelperComponent } from '../../concern/HelperComponent'

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
    renderer.create(
      <HelperComponent>
        <MainScreen navigation={null as never} />
      </HelperComponent>,
    )
    expect((NotificationListScreen as jest.Mock).mock.calls.length).toEqual(1)
  })
})

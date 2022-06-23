import React from 'react'
import { render } from '@testing-library/react-native'
import { HelperComponent } from 'tests/concern/HelperComponent'
import { NotificationResultScreen } from 'components/screens/notification/NotificationResultScreen'

describe('NotificationResultScreen', () => {
  it('displays notifications', () => {
    render(
      <HelperComponent>
        <NotificationResultScreen
          navigation={{ navigate: jest.fn() } as never}
          route={
            {
              params: { isConfirmation: true },
            } as never
          }
        />
      </HelperComponent>,
    )
  })
})

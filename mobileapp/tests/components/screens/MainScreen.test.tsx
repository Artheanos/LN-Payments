import React from 'react'

import { MainScreen } from 'components/screens/MainScreen'
import { StackNavigationProp } from '@react-navigation/stack'
import { SignInRouterProps } from 'components/routers/RouterPropTypes'
import { render, RenderAPI} from '@testing-library/react-native'

describe('Main screen', () => {
  let navigationMock: Partial<StackNavigationProp<SignInRouterProps>>
  let component: RenderAPI

  beforeEach(() => {
    navigationMock = {
      navigate: jest.fn(),
    }
    component = render(
      <MainScreen
        navigation={navigationMock as StackNavigationProp<SignInRouterProps>}
      />,
    )
  })

  it('renders correctly', () => {
    expect(component.getByText('HOME')).toBeFalsy()
  })
})

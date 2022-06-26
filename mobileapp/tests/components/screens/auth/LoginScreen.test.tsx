import React from 'react'
import {
  fireEvent,
  render,
  waitFor,
  screen,
} from '@testing-library/react-native'

import { HelperComponent } from 'tests/concern/HelperComponent'
import { LoginResponse } from 'webService/interface/auth'
import { LoginScreen } from 'components/screens/auth/LoginScreen'
import { Role } from 'webService/interface/user'
import { api } from 'webService/requests'

jest.mock('webService/requests', () => ({
  api: {
    auth: {
      tryLogin: jest.fn(),
    },
  },
}))

describe('LoginScreen', () => {
  const inputValues = {
    email: 'admin@admin.pl',
    hostUrl: 'http://10.0.2.2:8080',
    password: 'admin',
  }

  const responseData: LoginResponse = {
    ...inputValues,
    token: '123',
    role: Role.ADMIN,
    fullName: 'Mr. Admin',
    notificationChannelId: '123',
  }

  let tryLoginResponse: { data: unknown } = {
    data: responseData,
  }

  beforeEach(async () => {
    const mock = api.auth.tryLogin as jest.Mock
    mock.mockClear()
    mock.mockReturnValue(new Promise((resolve) => resolve(tryLoginResponse)))
    render(
      <HelperComponent>
        <LoginScreen />
      </HelperComponent>,
    )

    const emailInput = screen.getByPlaceholderText('Email')
    const passwordInput = screen.getByPlaceholderText('Password')
    const hostUrlInput = screen.getByPlaceholderText('HostUrl')
    const loginButton = screen.getByRole('button')

    await waitFor(() => {
      fireEvent.changeText(emailInput, inputValues.email)
      fireEvent.changeText(passwordInput, inputValues.password)
      fireEvent.changeText(hostUrlInput, inputValues.hostUrl)
      fireEvent.press(loginButton)
    })
  })

  describe('when input data is valid', () => {
    beforeAll(() => {
      responseData.role = Role.ADMIN
      inputValues.hostUrl = 'http://10.0.2.2:8080'
    })

    it('calls the api', async () => {
      expect(api.auth.tryLogin).toHaveBeenCalledWith(inputValues)
    })
  })

  describe('when the url is invalid', () => {
    beforeAll(() => {
      inputValues.hostUrl = 'invalid url'
    })

    it('displays error and does not call the api', () => {
      screen.getByText('Invalid url')
      expect(api.auth.tryLogin).not.toHaveBeenCalled()
    })
  })

  describe('when api returns a user with a user role', () => {
    beforeAll(() => {
      responseData.role = Role.USER
      inputValues.hostUrl = 'http://10.0.2.2:8080'
    })

    it('shows error', async () => {
      await waitFor(() => {
        screen.getByText('This app is for admins only')
      })
    })
  })

  describe('when does not return data', () => {
    beforeAll(() => {
      responseData.role = Role.ADMIN
      tryLoginResponse = { data: null }
      inputValues.hostUrl = 'http://10.0.2.2:8080'
    })

    it('displays error', async () => {
      await waitFor(() => {
        screen.getByText('Invalid credentials')
      })
    })
  })
})

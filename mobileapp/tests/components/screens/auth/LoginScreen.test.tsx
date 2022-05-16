import { render, fireEvent, waitFor } from '../../../testUtils'
import { RenderAPI } from '@testing-library/react-native'

import { LoginForm } from 'common-ts/webServiceApi/interface/auth'
import { LoginFormData } from 'components/screens/auth/LoginScreen/loginForm'
import { LoginScreen } from 'components/screens/auth/LoginScreen'
import { UserContext } from 'components/context/UserContext'

jest.mock('api', () => ({
  api: {
    auth: {
      tryLogin: ({ email }: LoginForm, url: string) => {
        return new Promise((resolve) => {
          if (url === 'http://wrong.url/') resolve({ status: 0 })

          if (email) {
            const role = email.includes('admin') ? 'ROLE_ADMIN' : 'ROLE_USER'
            return resolve({ data: { role } })
          }
          return resolve({ status: 400 })
        })
      },
    },
  },
}))

describe('LoginScreen', () => {
  let setToken = jest.fn()
  let renderResult: RenderAPI
  let fillForm: (data: LoginFormData) => Promise<unknown>

  beforeEach(() => {
    setToken = jest.fn()
    renderResult = render(
      <UserContext.Provider value={{ token: null, setToken }}>
        <LoginScreen />
      </UserContext.Provider>,
    )
    fillForm = ({ email, password, url }: LoginFormData) =>
      waitFor(async () => {
        fireEvent(
          renderResult.getByPlaceholderText('Email'),
          'onChangeText',
          email,
        )
        fireEvent(
          renderResult.getByPlaceholderText('Password'),
          'onChangeText',
          password,
        )
        fireEvent(
          renderResult.getByPlaceholderText('Host URL'),
          'onChangeText',
          url,
        )
        fireEvent.press(renderResult.getByRole('button'))
      })
  })

  describe('when user is an admin', () => {
    beforeEach(async () => {
      await fillForm({
        email: 'admin',
        password: 'password',
        url: 'http://localhost',
      })
    })

    it('sets the token', async () => {
      expect(setToken.mock.calls.length).toBe(1)
    })
  })

  describe('when user is not an admin', () => {
    beforeEach(async () => {
      await fillForm({
        email: 'user',
        password: 'password',
        url: 'http://localhost',
      })
    })

    it('shows error message', async () => {
      const { getByText } = renderResult

      expect(getByText('This app is for admins only')).toBeDefined()
      expect(setToken.mock.calls.length).toBe(0)
    })
  })

  describe('when user does not exist', () => {
    beforeEach(async () => {
      await fillForm({
        email: '',
        password: 'password',
        url: 'http://localhost',
      })
    })

    it('show error message', async () => {
      const { getByText } = renderResult

      expect(getByText('Invalid credentials')).toBeDefined()
      expect(setToken.mock.calls.length).toBe(0)
    })
  })

  describe('when url is invalid', () => {
    beforeEach(async () => {
      await fillForm({
        email: 'admin',
        password: 'password',
        url: 'test',
      })
    })

    it('show error message', async () => {
      const { getByText } = renderResult

      expect(getByText('Invalid url')).toBeDefined()
      expect(setToken.mock.calls.length).toBe(0)
    })
  })

  describe('when a server does not respond', () => {
    beforeEach(async () => {
      await fillForm({
        email: 'admin',
        password: 'password',
        url: 'http://wrong.url/',
      })
    })

    it('shows error message', async () => {
      const { getByText } = renderResult

      expect(getByText('Network error')).toBeDefined()
      expect(setToken.mock.calls.length).toBe(0)
    })
  })
})

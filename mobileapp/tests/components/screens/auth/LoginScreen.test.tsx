import { render, fireEvent, waitFor } from '../../../testUtils'

import { LoginScreen } from 'components/screens/auth/LoginScreen'
import { UserContext } from 'components/context/UserContext'
import { RenderAPI } from '@testing-library/react-native'

jest.mock('api', () => ({
  api: {
    auth: {
      login: ({ email }: { email: string }) => {
        return new Promise((resolve) => {
          if (email) {
            const role = email.includes('admin') ? 'ROLE_ADMIN' : 'ROLE_USER'
            return resolve({ data: { role } })
          }
          return resolve({})
        })
      },
    },
  },
}))

// jest.mock('common-ts/dist/webServiceApi/interface/auth', () => {
//   return null
// })

describe('LoginScreen', () => {
  let setToken = jest.fn()
  let renderResult: RenderAPI

  beforeEach(() => {
    setToken = jest.fn()
    renderResult = render(
      <UserContext.Provider value={{ token: null, setToken }}>
        <LoginScreen />
      </UserContext.Provider>,
    )
  })

  describe('when user is an admin', () => {
    beforeEach(async () => {
      const { getByRole, getByPlaceholderText } = renderResult

      await waitFor(async () => {
        fireEvent(getByPlaceholderText('Email'), 'onChangeText', 'admin')
        fireEvent(getByPlaceholderText('Password'), 'onChangeText', 'password')
        fireEvent.press(getByRole('button'))
      })
    })

    it('sets the token', async () => {
      expect(setToken.mock.calls.length).toBe(1)
    })
  })

  describe('when user is not an admin', () => {
    beforeEach(async () => {
      const { getByRole, getByPlaceholderText } = renderResult
      await waitFor(async () => {
        fireEvent(getByPlaceholderText('Email'), 'onChangeText', 'user')
        fireEvent(getByPlaceholderText('Password'), 'onChangeText', 'password')
        fireEvent.press(getByRole('button'))
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
      const { getByRole, getByPlaceholderText } = renderResult
      await waitFor(async () => {
        fireEvent(getByPlaceholderText('Email'), 'onChangeText', '')
        fireEvent(getByPlaceholderText('Password'), 'onChangeText', 'password')
        fireEvent.press(getByRole('button'))
      })
    })

    it('show error message', async () => {
      const { getByText } = renderResult

      expect(getByText('Invalid credentials')).toBeDefined()
      expect(setToken.mock.calls.length).toBe(0)
    })
  })
})

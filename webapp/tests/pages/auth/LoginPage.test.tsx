import { rest } from 'msw'
import { setupServer } from 'msw/node'

import routesBuilder from 'routesBuilder'
import { LoginPage } from 'pages/auth/LoginPage'
import {
  currentPath,
  fireEvent,
  render,
  screen,
  waitFor
} from 'tests/test-utils'

describe('LoginPage', () => {
  const server = setupServer(
    rest.post(routesBuilder.api.auth.login, (req, res, ctx) => {
      const { email, password } = req.body as {
        email: string
        password: string
      }
      if (email === 'test@test.pl' && password === 'Test1!') {
        return res(ctx.text('ok'))
      }
      return res(ctx.status(401))
    })
  )

  const fillForm = async (email: string, password: string) => {
    await waitFor(() => {
      fireEvent.change(screen.getByLabelText('Email'), {
        target: { value: email }
      })
      fireEvent.change(screen.getByLabelText('Password'), {
        target: { value: password }
      })
      screen.getByRole('button', { name: 'Login' }).click()
    })
  }

  beforeEach(() => {
    server.listen()
    render(<LoginPage />, { location: '/login' })
  })

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  describe('when credentials are valid', () => {
    beforeEach(async () => await fillForm('test@test.pl', 'Test1!'))

    it('redirects to landing page and shows success message', async () => {
      await waitFor(() => {
        expect(screen.getByText('Login successful')).toBeInTheDocument()
      })
      expect(currentPath()).toBe('/')
    })
  })

  describe('when credentials are invalid', () => {
    beforeEach(async () => await fillForm('test@test.pl', ''))

    it('does not redirect and shows error', async () => {
      await waitFor(() => {
        expect(
          screen.getByText('Email or password is invalid')
        ).toBeInTheDocument()
      })
      expect(currentPath()).toBe('/login')
    })
  })
})

import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { Route, Routes } from 'react-router-dom'

import routesBuilder from 'routesBuilder'
import { UserLayout } from 'components/Layouts/UserLayout'
import { UserProvider } from 'components/Context/UserContext'
import { render, screen, waitFor } from 'tests/test-utils'
import { getLocalJson, setLocalJson } from 'utils/persist'

const Consumer = () => {
  return <div>Consumer</div>
}

const renderLayout = () => {
  render(
    <UserProvider>
      <Routes>
        <Route element={<UserLayout />}>
          <Route element={<Consumer />} path="/" />
        </Route>
      </Routes>
    </UserProvider>
  )
}

describe('UserProvider', () => {
  const init = (token: string) => {
    setLocalJson('token', token)
    setLocalJson('user', { email: 'email@email.com' })
    renderLayout()
  }

  const validToken = '123'
  const server = setupServer(
    rest.get(routesBuilder.api.auth.refreshToken, (req, res, ctx) => {
      const isValid = getLocalJson('token') === validToken
      return res(ctx.json(isValid ? { token: '123' } : null))
    })
  )

  beforeAll(() => server.listen())
  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  describe('when token is valid', () => {
    beforeEach(() => init('123'))

    it('renders children', async () => {
      await waitFor(() => {
        expect(screen.getByText('Consumer')).toBeInTheDocument()
      })
    })
  })

  describe('when token is invalid', () => {
    it('does not render children', async () => {
      init('321')

      await waitFor(() => {
        expect(screen.queryByText('Consumer')).not.toBeInTheDocument()
      })
    })
  })
})

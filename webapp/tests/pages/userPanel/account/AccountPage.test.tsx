import { setupServer } from 'msw/node'
import { rest } from 'msw'
import { routes } from 'webService/routes'
import { render, screen, waitFor } from 'tests/test-utils'
import { Role, User } from 'webService/interface/user'
import { AccountPage } from 'pages/userPanel/account/AccountPage'

describe('AccountPageTests', () => {
  let status = 200

  const server = setupServer(
    rest.get(routes.users.index, (req, res, ctx) => {
      return res(ctx.status(status), ctx.json(user))
    })
  )

  const user: User = {
    email: 'admin@admin.pl',
    fullName: 'Admin',
    role: Role.ADMIN
  }

  beforeEach(async () => {
    server.listen()
    render(<AccountPage />)
    await screen.findByText('Account details')
  })

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  it('should render the information', async () => {
    await waitFor(() => {
      expect(screen.getByLabelText('Name')).toHaveValue(user.fullName)
      expect(screen.getByLabelText('Email')).toHaveValue(user.email)
      expect(screen.getByLabelText('Role')).toHaveValue(user.role)
      expect(screen.getByText('Change password')).toBeInTheDocument()
    })
  })

  describe('errors', () => {
    beforeAll(() => {
      status = 500
    })
    it('should display an error when error occurred', async () => {
      await waitFor(() => {
        expect(
          screen.getByText('Error, please try again later.')
        ).toBeInTheDocument()
      })
    })
  })
})

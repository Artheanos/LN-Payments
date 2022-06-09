import { rest } from 'msw'
import { setupServer } from 'msw/node'

import routesBuilder from 'routesBuilder'
import {
  currentPath,
  fireEvent,
  render,
  screen,
  waitFor
} from 'tests/test-utils'
import { AdminCreatePage } from 'pages/userPanel/adminManagement/AdminCreatePage'
import { RegisterForm } from 'webService/interface/auth'

describe('AdminCreatePage', () => {
  const server = setupServer(
    rest.post(routesBuilder.api.admins.index, (req, res, ctx) => {
      const { email } = req.body as RegisterForm
      if (email === 'test@test.pl') {
        return res(ctx.status(409))
      }
      return res(ctx.status(201))
    })
  )

  const fillForm = async (
    fullName: string,
    email: string,
    password: string
  ) => {
    await waitFor(() => {
      fireEvent.change(screen.getByLabelText('Email'), {
        target: { value: email }
      })
      fireEvent.change(screen.getByLabelText('Full name'), {
        target: { value: fullName }
      })
      fireEvent.change(screen.getByLabelText('Password'), {
        target: { value: password }
      })
      fireEvent.change(screen.getByLabelText('Repeat your password'), {
        target: { value: password }
      })
    })
    await waitFor(() => screen.getByRole('button', { name: 'Submit' }).click())
  }

  beforeEach(() => {
    server.listen()
    render(<AdminCreatePage />, { location: '/panel/admins/new' })
  })

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  describe('when params are valid', () => {
    beforeEach(async () => await fillForm('Jan', 'test2@test.pl', 'TestTest1!'))

    it('redirects to admins list and shows success message', async () => {
      await waitFor(() => {
        expect(
          screen.getByText('New admin has been created')
        ).toBeInTheDocument()
      })
      expect(currentPath()).toBe('/panel/admins')
    })
  })

  describe('when email has been taken', () => {
    beforeEach(async () => await fillForm('Jan', 'test@test.pl', 'TestTest1!'))

    it('stays on the page and shows error message', async () => {
      await waitFor(() => {
        expect(
          screen.getByText('Given email is already in use')
        ).toBeInTheDocument()
      })
      expect(currentPath()).toBe('/panel/admins/new')
    })
  })
})

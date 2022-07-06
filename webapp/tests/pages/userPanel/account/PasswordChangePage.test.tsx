import { setupServer } from 'msw/node'
import { rest } from 'msw'
import { routes } from 'webService/routes'
import { fireEvent, render, screen, waitFor } from 'tests/test-utils'
import { PasswordChangePage } from 'pages/userPanel/account/PasswordChangePage'

describe('PasswordChangePage', () => {
  let status = 200

  const server = setupServer(
    rest.put(routes.users.password, (req, res, ctx) => {
      return res(ctx.status(status))
    })
  )

  beforeEach(async () => {
    server.listen()
    render(<PasswordChangePage />)
    await screen.findByText('Change password')
  })

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  it('should update password', async () => {
    await waitFor(() => {
      fireEvent.change(screen.getByLabelText('Current password'), {
        target: { value: 'admin' }
      })
      fireEvent.change(screen.getByLabelText('New password'), {
        target: { value: 'zaq1@WSX' }
      })
      fireEvent.change(screen.getByLabelText('Confirm new password'), {
        target: { value: 'zaq1@WSX' }
      })
    })
    screen.getByText('Submit').click()

    await waitFor(() => {
      expect(screen.getByText('Password updated!')).toBeInTheDocument()
    })
  })

  it('should display error when tried to reuse password', async () => {
    await waitFor(() => {
      fireEvent.change(screen.getByLabelText('Current password'), {
        target: { value: 'zaq1@WSX' }
      })
      fireEvent.change(screen.getByLabelText('New password'), {
        target: { value: 'zaq1@WSX' }
      })
      fireEvent.change(screen.getByLabelText('Confirm new password'), {
        target: { value: 'zaq1@WSX' }
      })
    })
    screen.getByText('Submit').click()

    await waitFor(() => {
      expect(
        screen.getByText('You can not reuse the old password')
      ).toBeInTheDocument()
    })
  })

  it('should display message when password does not match', async () => {
    await waitFor(() => {
      fireEvent.change(screen.getByLabelText('Current password'), {
        target: { value: 'zaq1W!SX' }
      })
      fireEvent.change(screen.getByLabelText('New password'), {
        target: { value: 'zaq1@WSX' }
      })
      fireEvent.change(screen.getByLabelText('Confirm new password'), {
        target: { value: 'zaq1@@WSX' }
      })
    })
    screen.getByText('Submit').click()

    await waitFor(() => {
      expect(screen.getByText('Passwords does not match!')).toBeInTheDocument()
    })
  })

  it('should display error when required fields are empty', async () => {
    screen.getByText('Submit').click()
    await waitFor(() => {
      expect(
        screen.getByText('Current password is required')
      ).toBeInTheDocument()
      expect(screen.getByText('New password is required')).toBeInTheDocument()
      expect(screen.getByText('Passwords does not match!')).toBeInTheDocument()
    })
  })

  describe('Backend returns 409', () => {
    beforeAll(() => {
      status = 409
    })

    it('should display message that current password is incorrect', async () => {
      await waitFor(() => {
        fireEvent.change(screen.getByLabelText('Current password'), {
          target: { value: 'zaq1W!SX' }
        })
        fireEvent.change(screen.getByLabelText('New password'), {
          target: { value: 'zaq1@WSX' }
        })
        fireEvent.change(screen.getByLabelText('Confirm new password'), {
          target: { value: 'zaq1@WSX' }
        })
      })
      screen.getByText('Submit').click()
      await waitFor(() => {
        expect(
          screen.getByText('Current password does not match your password.')
        ).toBeInTheDocument()
      })
    })
  })

  describe('Backend returns 400', () => {
    beforeAll(() => {
      status = 400
    })

    it('should display error when backend responds with an error', async () => {
      await waitFor(() => {
        fireEvent.change(screen.getByLabelText('Current password'), {
          target: { value: 'zaq1W!SX' }
        })
        fireEvent.change(screen.getByLabelText('New password'), {
          target: { value: 'zaq1@WSX' }
        })
        fireEvent.change(screen.getByLabelText('Confirm new password'), {
          target: { value: 'zaq1@WSX' }
        })
      })
      screen.getByText('Submit').click()
      await waitFor(() => {
        expect(
          screen.getByText('Error while updating password.')
        ).toBeInTheDocument()
        expect(
          screen.getByText('Error, please try again later.')
        ).toBeInTheDocument()
      })
    })
  })
})
